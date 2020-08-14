package com.lisowski.clientapp.activities

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.PolyUtil
import com.lisowski.clientapp.API.ApiClient
import com.lisowski.clientapp.Constants.RIDE_DETAIL
import com.lisowski.clientapp.R
import com.lisowski.clientapp.Utils.SharedPreferencesManager
import com.lisowski.clientapp.models.Car
import com.lisowski.clientapp.models.Message
import com.lisowski.clientapp.models.RideDetailResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.concurrent.TimeUnit

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    enum class TimerState {
        Stopped, Paused, Running
    }

    private lateinit var mMap: GoogleMap
    private val MAPS_ACTIVITY: String = "MapsActivity"
    private lateinit var details: RideDetailResponse
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var sessionManager: SharedPreferencesManager
    private lateinit var disposableLoc: Disposable
    private lateinit var disposableRideStatus: Disposable
    private lateinit var apiClient: ApiClient
    private val context: Context = this
    private var timeLeftInMs: Long = 1000
    private var timerState = TimerState.Stopped
    private var driverId: Long = -1
    private var rideId: Long = -1
    private var driverMarker: Marker? = null
    private lateinit var driverPolyline : Polyline
    private lateinit var car: Car

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        apiClient = ApiClient()
        sessionManager = SharedPreferencesManager(this)

        details = intent.getParcelableExtra(RIDE_DETAIL)!!

        Log.d(MAPS_ACTIVITY, "onCreate: $details")
        timeLeftInMs *= details.driverDuration

        getCarInfo(details.idDriver)
        saveRideIdAndDriverIdInSP()
        startTimer()
        initDisposableLoc()
        initDisposableRideStatus()
        hideConfirmCard()

        confirmArriveBnt.setOnClickListener {
            confirmDriverArrive()
            initDisposableRideStatus()
            hideConfirmCard()
            hideTimeCounterCard()
            removeDriverPolyLine()
        }
    }

    private fun removeDriverPolyLine() {
        driverPolyline.remove()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val userLoc: List<Double> = details.userLocation.split(",").map { it.toDouble() }
        val userMarker = LatLng(userLoc[0], userLoc[1])
        val userLocDest: List<Double> = details.userDestination.split(",").map { it.toDouble() }
        val userDest = LatLng(userLocDest[0], userLocDest[1])
        mMap.addMarker(MarkerOptions().position(userMarker).title("Punkt początkowy"))
        mMap.addMarker(MarkerOptions().position(userDest).title("Punkt docelowy"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 13f))

        val userPolyline = googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .addAll(decodePolyline(details.userPolyline))
                .color(Color.BLUE)
                .width(5F)
        )
        userPolyline.tag = "userPoly"

        driverPolyline = googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .addAll(decodePolyline(details.driverPolyline))
                .color(Color.RED)
                .width(5F)
        )
        driverPolyline.tag = "driverPoly"

        googleMap.setOnPolylineClickListener(this)
    }

    private fun getCarInfo(idDriver: Long) {
        val observable = apiClient.getApiService()
            .getDriverCar(token = "Bearer ${sessionManager.fetchAuthToken()}", rideId = idDriver)
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> carResponse(response) }, { t -> onFailure(t) })
    }

    private fun carResponse(response: Car?) {
        Log.d(MAPS_ACTIVITY, "carResponse: $response")
        car = response!!
        carBrandTV.text = car.carBrand
        carModelTV.text = car.carModel
        carColorTV.text = car.color
        carRegNumTV.text = car.registrationNumber
    }

    private fun confirmDriverArrive() {
        val observable = apiClient.getApiService()
            .confirmDriverArrive(
                token = "Bearer ${sessionManager.fetchAuthToken()}",
                rideId = rideId
            )
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> statusOnResponse(response) }, { t -> onFailure(t) })
    }
    private fun getPriceRequest() {
        val observable = apiClient.getApiService()
            .getPriceForRide(
                token = "Bearer ${sessionManager.fetchAuthToken()}",
                id_ride = rideId
            )
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> getPriceFromResponse(response) }, { t -> onFailure(t) })
    }

    private fun getPriceFromResponse(response: Message?) {
        //TODO: wyświetlic banerz buttonem
        Log.d(MAPS_ACTIVITY, "getPriceFromResponse: $response")
    }

    private fun initDisposableRideStatus() {
        disposableRideStatus = Observable.interval(
            1000, 5000,
            TimeUnit.MILLISECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { callRideStatus() }
    }

    private fun initDisposableLoc() {
        disposableLoc = Observable.interval(
            1000, 15000,
            TimeUnit.MILLISECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { callDriverPosition() }
    }

    private fun saveRideIdAndDriverIdInSP() {
        driverId = details.idDriver
        sessionManager.saveDriverId(driverId)
        rideId = details.idRide
        sessionManager.saveRideId(rideId)
    }

    private fun callRideStatus() {
        Log.d(MAPS_ACTIVITY, "callRideStatus: ride id $rideId")
        val observable = apiClient.getApiService()
            .getRideStatus(token = "Bearer ${sessionManager.fetchAuthToken()}", rideId = rideId)
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> statusOnResponse(response) }, { t -> onFailure(t) })
    }

    private fun statusOnResponse(response: Message?) {
        Log.d(MAPS_ACTIVITY, "statusOnResponse: $response")
        if (response!!.msg == "WAITING_FOR_USER") {
            showDialog()
            showConfirmCard()
            changeInfoOnTimeCounterCard()
            disposableRideStatus.dispose()
        }
        if (response.msg == "ENDING") {
            //TODO zatrzymac sprawdzaie STATUSU  wyświetli się okno z info ile do zapłaty. KLIK ZAPłać /setStatus a potemjak chce wystaw ocene /setRating
            getPriceRequest()
        }
    }

    private fun changeInfoOnTimeCounterCard() {
        infoCounterTV.text = "Kierowca czeka na Ciebie"
        minCounterTV.text = " "
    }

    private fun showConfirmCard() {
        confirmArriveCard.visibility = View.VISIBLE
    }

    private fun hideConfirmCard() {
        confirmArriveCard.visibility = View.GONE
    }
    private fun hideTimeCounterCard() {
        timeCounterCard.visibility = View.GONE
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Kierowca czeka na Ciebie")
            .setPositiveButton("OK") { _, _ ->
                // Respond to positive button press
            }
            .show()
    }

    private fun callDriverPosition() {
        val observable = apiClient.getApiService()
            .getDriverLoc(token = "Bearer ${sessionManager.fetchAuthToken()}", driverID = driverId)
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> locationResponse(response) }, { t -> onFailure(t) })
    }

    private fun onFailure(t: Throwable?) {
        Log.d(MAPS_ACTIVITY, "onFailure: ${t!!.message}")
    }

    private fun locationResponse(response: Message?) {
        if (response != null) {
            Log.d(MAPS_ACTIVITY, "locationResponse: ${response.msg} ")
            val driverLoc: List<Double> = response.msg.split(",").map { it.toDouble() }
            val userMarker = LatLng(driverLoc[0], driverLoc[1])
            if (driverMarker == null) {
                driverMarker =
                    mMap.addMarker(MarkerOptions().position(userMarker).title("Kierowca"))
            } else
                driverMarker!!.position = userMarker
        }
    }

    override fun onPause() {
        super.onPause()
        if (timerState == TimerState.Running) {
            stopTimer()
            sessionManager.saveTimerData(
                timeLeft = timeLeftInMs,
                pausedTime = System.currentTimeMillis(),
                timerState = TimerState.Paused
            )
        }
        disposableLoc.dispose()
        disposableRideStatus.dispose()
    }

    override fun onResume() {
        super.onResume()
        val timerStateId = sessionManager.fetchTimeState()!!

        if (timerStateId > -1)
            timerState = TimerState.values()[timerStateId]

        if (timerState == TimerState.Paused) {
            timeLeftInMs = sessionManager.fetchTimeLeft()!!
            val pausedTime = sessionManager.fetchPausedTime()!!
            if (timeLeftInMs > 0 && pausedTime > 0) {
                val difference = System.currentTimeMillis() - pausedTime
                if (timeLeftInMs - difference > 0) {
                    timeLeftInMs -= difference
                    startTimer()
                } else
                    minCounterTV.text = "0 min"
            }
        }

        loadDriverIDandRideIDfromSP()

        if (disposableLoc.isDisposed) {
            initDisposableLoc()
        }
        if (disposableRideStatus.isDisposed) {
            initDisposableRideStatus()
        }
    }

    private fun loadDriverIDandRideIDfromSP() {
        val id = sessionManager.fetchDriverId()!!
        if (id != -1L)
            driverId = id
        val rideID = sessionManager.fetchRideId()!!
        if (rideID != -1L)
            rideId = rideID
    }



    private fun decodePolyline(polyline: String): List<LatLng> {
        return PolyUtil.decode(polyline)
    }

    override fun onPolylineClick(poly: Polyline?) {
        if (poly?.width == 5f)
            poly.width = 15f
        else
            poly?.width = 5f
    }

    private fun startTimer() {
        timerState = TimerState.Running
        countDownTimer = object : CountDownTimer(timeLeftInMs, 60000) {
            override fun onFinish() {
                Log.d(MAPS_ACTIVITY, "CountDownTimer: finish ")
                sessionManager.saveTimerData(
                    timeLeft = -1,
                    pausedTime = -1,
                    timerState = TimerState.Stopped
                )
                timerState = TimerState.Stopped
            }

            override fun onTick(tick: Long) {
                timeLeftInMs = tick
                updateTimer()
            }

        }.start()
    }

    private fun updateTimer() {
        var sec = timeLeftInMs / 60000
        minCounterTV.text = sec.toString()
        minCounterTV.append(" min")
        Log.d(MAPS_ACTIVITY, "updateTimer: $sec")
    }

    private fun stopTimer() {
        countDownTimer.cancel()
    }
}