package com.lisowski.clientapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.PolyUtil
import com.lisowski.clientapp.API.ApiClient
import com.lisowski.clientapp.Constants.COMPLETE
import com.lisowski.clientapp.Constants.RIDE_DETAIL
import com.lisowski.clientapp.Constants.USER_DEST
import com.lisowski.clientapp.Constants.USER_LOC
import com.lisowski.clientapp.Constants.USER_POLYLINE
import com.lisowski.clientapp.R
import com.lisowski.clientapp.Utils.SharedPreferencesManager
import com.lisowski.clientapp.models.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.drawerLayout
import kotlinx.android.synthetic.main.activity_maps.navigationView
import java.util.concurrent.TimeUnit

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener {

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
    private lateinit var driverPolyline: Polyline
    private lateinit var userPolyline: Polyline
    private lateinit var car: Car
    private var fromHistory = true
    private lateinit var userDestFromHist: String
    private lateinit var userLocFromHist: String
    private lateinit var userPolyFromHist: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        apiClient = ApiClient()
        sessionManager = SharedPreferencesManager(this)

        //drawerConf
        initToolBarAndDrawer()

        val getDetails: RideDetailResponse? = intent.getParcelableExtra(RIDE_DETAIL)
        if (getDetails != null) {
            fromHistory = false
            details = getDetails
            Log.d(MAPS_ACTIVITY, "onCreate: $details")
        }

        val uDest = intent.getStringExtra(USER_DEST)
        val uPoly = intent.getStringExtra(USER_POLYLINE)
        val uLoc = intent.getStringExtra(USER_LOC)
        if (uDest != null)
            userDestFromHist = uDest
        if (uLoc != null)
            userLocFromHist = uLoc
        if (uPoly != null)
            userPolyFromHist = uPoly

        if (!fromHistory) {
            getCarInfo(details.idDriver)
            timeLeftInMs *= details.driverDuration
            saveRideIdAndDriverIdInSP()
            startTimer()
            initDisposableLoc()
            initDisposableRideStatus()
        } else {
            hideTimeCounterCard()
        }

        hideConfirmCard()
        hidePaymentCard()
        hideRateCard()

        confirmArriveBnt.setOnClickListener {
            confirmDriverArrive()
            initDisposableRideStatus()
            hideConfirmCard()
            hideTimeCounterCard()
            removeDriverPolyLine()
            changeWidthUserPolyline()
        }
        confirmPaymentBnt.setOnClickListener {
            hidePaymentCard()
            setRideStatusToComplete()
            showRateCard()
        }
        rateBnt.setOnClickListener {
            var id = 0
            if (radio_button_1.isChecked) id = 1
            if (radio_button_2.isChecked) id = 2
            if (radio_button_3.isChecked) id = 3
            if (radio_button_4.isChecked) id = 4
            if (radio_button_5.isChecked) id = 5
            Log.d(MAPS_ACTIVITY, "onCreate: checkedRadioButtonId: $id")
            saveRideRating(id)
            startOrderActivity()
            showToast()
        }
    }

    private fun initToolBarAndDrawer() {
        setSupportActionBar(findViewById(R.id.toolbar))
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (!fromHistory) {
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

    }

    override fun onPause() {
        super.onPause()
        if (!fromHistory) {
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

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val userLoc: List<Double>
        val userLocDest: List<Double>
        if (!fromHistory) {
            userLoc = details.userLocation.split(",").map { it.toDouble() }
            userLocDest = details.userDestination.split(",").map { it.toDouble() }

            userPolyline = googleMap.addPolyline(
                PolylineOptions()
                    .clickable(true)
                    .addAll(decodePolyline(details.userPolyline))
                    .color(Color.BLUE)
                    .width(5F)
            )

            driverPolyline = googleMap.addPolyline(
                PolylineOptions()
                    .clickable(true)
                    .addAll(decodePolyline(details.driverPolyline))
                    .color(Color.RED)
                    .width(15F)
            )

        } else {
            userLoc = userLocFromHist.split(",").map { it.toDouble() }
            userLocDest = userDestFromHist.split(",").map { it.toDouble() }
            userPolyline = googleMap.addPolyline(
                PolylineOptions()
                    .clickable(true)
                    .addAll(decodePolyline(userPolyFromHist))
                    .color(Color.BLUE)
                    .width(15F)
            )
        }

        val userMarker = LatLng(userLoc[0], userLoc[1])
        val userDest = LatLng(userLocDest[0], userLocDest[1])
        val startMarkerDrawable: Drawable = resources.getDrawable(R.drawable.start_marker)
        val startIcon: BitmapDescriptor = getMarkerIconFromDrawable(startMarkerDrawable)
        val endMarkerDrawable: Drawable = resources.getDrawable(R.drawable.flag)
        val endIcon: BitmapDescriptor = getMarkerIconFromDrawable(endMarkerDrawable)

        mMap.addMarker(
            MarkerOptions().position(userMarker).title("Punkt początkowy").icon(startIcon)
        )
        mMap.addMarker(MarkerOptions().position(userDest).title("Punkt docelowy").icon(endIcon))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 13f))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (fromHistory) {
            when (item.itemId) {
                R.id.nav_order -> {
                    Log.d(MAPS_ACTIVITY, "onNavigationItemSelected: 1")
                    val intent = Intent(applicationContext, OrderActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_history -> {
                    Log.d(MAPS_ACTIVITY, "onNavigationItemSelected: 1")
                val intent = Intent(applicationContext, HistoryActivity::class.java)
                startActivity(intent)
                }
                R.id.nav_logout -> {
                    Log.d(MAPS_ACTIVITY, "onNavigationItemSelected: 3")
                    sessionManager.clear()
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
        val canvas: Canvas = Canvas()
        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private fun locationResponse(response: Message?) {
        if (response != null) {
            Log.d(MAPS_ACTIVITY, "locationResponse: ${response.msg} ")
            val driverLoc: List<Double> = response.msg.split(",").map { it.toDouble() }
            val userMarker = LatLng(driverLoc[0], driverLoc[1])
            if (driverMarker == null) {
                val carMarkerDrawable: Drawable = resources.getDrawable(R.drawable.ic_action_name)
                val carIcon: BitmapDescriptor = getMarkerIconFromDrawable(carMarkerDrawable)
                driverMarker =
                    mMap.addMarker(
                        MarkerOptions().position(userMarker).title("Kierowca").icon(carIcon)
                    )
            } else
                driverMarker!!.position = userMarker
        }
    }

    private fun saveRideRating(rate: Int) {
        Log.d(MAPS_ACTIVITY, "saveRideRating: rate $rate")
        val observable = apiClient.getApiService()
            .setRideRating(
                token = "Bearer ${sessionManager.fetchAuthToken()}",
                request = RideRating(id = rideId, rate = rate)
            )
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> statusOnResponse(response) }, { t -> onFailure(t) })
    }

    private fun setRideStatusToComplete() {
        val observable = apiClient.getApiService()
            .setRideToComplete(
                token = "Bearer ${sessionManager.fetchAuthToken()}",
                request = StatusMessage(id = rideId, status = COMPLETE)
            )
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> statusOnResponse(response) }, { t -> onFailure(t) })
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

    private fun startOrderActivity() {
        val intent = Intent(applicationContext, OrderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(RIDE_DETAIL, details)
        startActivity(intent)
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
            stopTimer()
            disposableRideStatus.dispose()
        }
        if (response.msg == "ENDING") {
            getPriceRequest()
            stopCheckingLocationAndStatus()
        }
    }

    private fun callDriverPosition() {
        val observable = apiClient.getApiService()
            .getDriverLoc(token = "Bearer ${sessionManager.fetchAuthToken()}", driverID = driverId)
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> locationResponse(response) }, { t -> onFailure(t) })
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

    private fun loadDriverIDandRideIDfromSP() {
        val id = sessionManager.fetchDriverId()!!
        if (id != -1L)
            driverId = id
        val rideID = sessionManager.fetchRideId()!!
        if (rideID != -1L)
            rideId = rideID
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

    private fun showDialog() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Kierowca czeka na Ciebie")
            .setPositiveButton("OK") { _, _ ->
                // Respond to positive button press
            }
            .show()
    }

    private fun updateTimer() {
        var sec = timeLeftInMs / 60000
        minCounterTV.text = sec.toString()
        minCounterTV.append(" min")
        Log.d(MAPS_ACTIVITY, "updateTimer: $sec")
    }

    private fun getPriceFromResponse(response: Message?) {
        if (response != null) {
            showPriceCard(response.msg)
        }
        Log.d(MAPS_ACTIVITY, "getPriceFromResponse: $response")
    }

    private fun showPriceCard(msg: String) {
        payCard.visibility = View.VISIBLE
        finalPriceTV.text = msg
    }

    private fun stopCheckingLocationAndStatus() {
        disposableRideStatus.dispose()
        disposableLoc.dispose()
    }

    private fun changeInfoOnTimeCounterCard() {
        infoCounterTV.text = "Kierowca czeka na Ciebie"
        minCounterTV.text = " "
    }

    private fun showConfirmCard() {
        confirmArriveCard.visibility = View.VISIBLE
    }

    private fun removeDriverPolyLine() {
        driverPolyline.remove()
    }

    private fun hideConfirmCard() {
        confirmArriveCard.visibility = View.GONE
    }

    private fun hidePaymentCard() {
        payCard.visibility = View.GONE
    }

    private fun hideTimeCounterCard() {
        timeCounterCard.visibility = View.GONE
    }

    private fun onFailure(t: Throwable?) {
        Log.d(MAPS_ACTIVITY, "onFailure: ${t!!.message}")
    }

    private fun decodePolyline(polyline: String): List<LatLng> {
        return PolyUtil.decode(polyline)
    }

    private fun stopTimer() {
        countDownTimer.cancel()
    }

    private fun showRateCard() {
        rateCard.visibility = View.VISIBLE
    }

    private fun hideRateCard() {
        rateCard.visibility = View.GONE
    }

    private fun changeWidthUserPolyline() {
        userPolyline.width = 15F
    }

    private fun showToast() {
        Toast.makeText(applicationContext, "Ocena zapisana", Toast.LENGTH_LONG)
            .show()
    }
}