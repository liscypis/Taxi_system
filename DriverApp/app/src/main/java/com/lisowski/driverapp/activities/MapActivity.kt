package com.lisowski.driverapp.activities

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.navigation.NavigationView
import com.google.maps.android.PolyUtil
import com.lisowski.clientapp.API.ApiClient
import com.lisowski.driverapp.Constants.COMPLETE
import com.lisowski.driverapp.R
import com.lisowski.driverapp.Utils.SharedPreferencesManager
import com.lisowski.driverapp.models.Message
import com.lisowski.driverapp.models.RideDetailResponse
import com.lisowski.driverapp.models.RideRating
import com.lisowski.driverapp.models.StatusMessage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history.drawerLayout
import kotlinx.android.synthetic.main.activity_history.navigationView
import kotlinx.android.synthetic.main.activity_map.*
import java.util.concurrent.TimeUnit
import kotlin.math.log

class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener {
    enum class Status {
        offline, Zajęty, Online
    }

    private val MAPS_ACTIVITY: String = "MapsActivity"
    private lateinit var mMap: GoogleMap
    private lateinit var details: RideDetailResponse
    private lateinit var sessionManager: SharedPreferencesManager
    private lateinit var disposableLoc: Disposable
    private lateinit var disposableRideStatus: Disposable
    private lateinit var disposableNewRide: Disposable
    private lateinit var apiClient: ApiClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userDestFromHist: String
    private lateinit var userLocFromHist: String
    private lateinit var userPolyFromHist: String
    private lateinit var driverPolyFromHist: String
    private lateinit var driverPolyline: Polyline
    private lateinit var userPolyline: Polyline
    private var previousLocation: LatLng? = null
    private var marker: Marker? = null
    private val context: Context = this
    private var driverId: Long = -1
    private var rideId: Long = -1
    private var fromHistory = true
    private var status: Status = Status.offline


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()


        apiClient = ApiClient()
        sessionManager = SharedPreferencesManager(this)

        //drawerConf
        initToolBarAndDrawer()


        driverId = sessionManager.fetchUserId()!!

        hideWaitCard()
        hideInfoCard()
        hidePaymentCard()
        hideRateCard()
        hideChangeStatusCard()

        statusTV.text = status.name


        confirmPaymentBnt.setOnClickListener {
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
            showToast()
        }
        statusBnt.setOnClickListener {
            if (statusRD1.isChecked) status = Status.offline
            if (statusRD2.isChecked) status = Status.Zajęty
            if (statusRD3.isChecked) status = Status.Online
            Log.d(MAPS_ACTIVITY, "onCreate: checkedRadioButtonId: $status")
            getStatusFromRB()
            hideChangeStatusCard()
        }
        statusTV.setOnClickListener {
            showChangeStatusCard()
        }
    }

    private fun showChangeStatusCard() {
        changeStatusCard.visibility = View.VISIBLE
    }

    private fun getStatusFromRB() {
        statusTV.text = status.name
        if (status == Status.offline) {
            disposableLoc.dispose()

            //TODO wyłączyć nasłuchiwania nowych ride
        }
        if (status == Status.Zajęty) {
            //TODO wyłączyć nasłuchiwania nowych ride
        }

        if (status == Status.Online) {
            initLocationListener()
            initNewRideListener()
        }

    }

    private fun initLocationListener() {
        disposableLoc = Observable.interval(
            1000, 2000,
            TimeUnit.MILLISECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { getLoc() }
    }

    private fun initNewRideListener() {
        disposableNewRide = Observable.interval(
            1000, 10000,
            TimeUnit.MILLISECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { getNewRide() }
    }

    private fun getNewRide() {
        val observable = apiClient.getApiService()
            .checkForNewRide(
                token = "Bearer ${sessionManager.fetchAuthToken()}",
                id = driverId
            )
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> rideResponse(response) }, { t -> onFailure(t) })
    }

    private fun rideResponse(response: RideDetailResponse?) {
        if (response != null && response.idRide != 0L) {
            Log.d(MAPS_ACTIVITY, "rideResponse: $response")
            //TODO dodać lementy na mapę oraz sprawdzić jeszcze jaki jest typ RIDE!!!
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 1)
    }

    private fun getLoc() {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(MAPS_ACTIVITY, "onCreate: ${location.toString()}")
                changeMarkLocation(location!!)
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
        Log.d(MAPS_ACTIVITY, "onResume: :D")
        if (this::disposableLoc.isInitialized)
            if (disposableLoc.isDisposed)
                initLocationListener()


    }

    override fun onPause() {
        super.onPause()
        if (this::disposableLoc.isInitialized)
            disposableLoc.dispose()
        Log.d(MAPS_ACTIVITY, "onPause: xDDD")
    }

    private fun changeMarkLocation(location: Location) {
        val loc: LatLng = LatLng(location.latitude, location.longitude)
        if (previousLocation == null || loc != previousLocation)
            if (marker == null) {
                val carMarkerDrawable: Drawable = resources.getDrawable(R.drawable.taxi)
                val carIcon: BitmapDescriptor = getMarkerIconFromDrawable(carMarkerDrawable)
                marker =
                    mMap.addMarker(
                        MarkerOptions().position(loc).title("Kierowca").icon(carIcon)
                    )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17f))
                previousLocation = loc
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17f))
                marker!!.position = loc
                previousLocation = loc
            }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val userLoc: List<Double>
        val userLocDest: List<Double>
//        if (!fromHistory) {
//            userLoc = details.userLocation.split(",").map { it.toDouble() }
//            userLocDest = details.userDestination.split(",").map { it.toDouble() }
//
//            userPolyline = googleMap.addPolyline(
//                PolylineOptions()
//                    .clickable(true)
//                    .addAll(decodePolyline(details.userPolyline))
//                    .color(Color.BLUE)
//                    .width(5F)
//            )
//
//            driverPolyline = googleMap.addPolyline(
//                PolylineOptions()
//                    .clickable(true)
//                    .addAll(decodePolyline(details.driverPolyline))
//                    .color(Color.RED)
//                    .width(15F)
//            )
//
//        } else {
//            userLoc = userLocFromHist.split(",").map { it.toDouble() }
//            userLocDest = userDestFromHist.split(",").map { it.toDouble() }
//            userPolyline = googleMap.addPolyline(
//                PolylineOptions()
//                    .clickable(true)
//                    .addAll(decodePolyline(userPolyFromHist))
//                    .color(Color.BLUE)
//                    .width(15F)
//            )
//        }
//
//        val userMarker = LatLng(userLoc[0], userLoc[1])
//        val userDest = LatLng(userLocDest[0], userLocDest[1])
//        val startMarkerDrawable: Drawable = resources.getDrawable(R.drawable.start_marker)
//        val startIcon: BitmapDescriptor = getMarkerIconFromDrawable(startMarkerDrawable)
//        val endMarkerDrawable: Drawable = resources.getDrawable(R.drawable.flag)
//        val endIcon: BitmapDescriptor = getMarkerIconFromDrawable(endMarkerDrawable)
//
//        mMap.addMarker(
//            MarkerOptions().position(userMarker).title("Punkt początkowy").icon(startIcon)
//        )
//        mMap.addMarker(MarkerOptions().position(userDest).title("Punkt docelowy").icon(endIcon))
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker, 13f))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (fromHistory) {
            when (item.itemId) {
                R.id.nav_order -> {
//                    Log.d(MAPS_ACTIVITY, "onNavigationItemSelected: 1")
//                    val intent = Intent(applicationContext, OrderActivity::class.java)
//                    startActivity(intent)
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

            disposableRideStatus.dispose()
        }
        if (response.msg == "ENDING") {
            getPriceRequest()
        }
    }


    private fun initDisposableRideStatus() {
        disposableRideStatus = Observable.interval(
            1000, 3000,
            TimeUnit.MILLISECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { callRideStatus() }
    }

    private fun sendLocation() {
        Log.d(MAPS_ACTIVITY, "sendLocation: dupa")
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

    private fun removeDriverPolyLine() {
        driverPolyline.remove()
    }


    private fun onFailure(t: Throwable?) {
        Log.d(MAPS_ACTIVITY, "onFailure: ${t!!.message}")
    }

    private fun decodePolyline(polyline: String): List<LatLng> {
        return PolyUtil.decode(polyline)
    }


    private fun showRateCard() {
        rateCard.visibility = View.VISIBLE
    }

    private fun hideRateCard() {
        rateCard.visibility = View.GONE
    }

    private fun hidePaymentCard() {
        payCard.visibility = View.GONE
    }

    private fun hideWaitCard() {
        waitCard.visibility = View.GONE
    }

    private fun hideInfoCard() {
        infoCard.visibility = View.GONE
    }

    private fun hideChangeStatusCard() {
        changeStatusCard.visibility = View.GONE
    }

    private fun showToast() {
        Toast.makeText(applicationContext, "Ocena zapisana", Toast.LENGTH_LONG)
            .show()
    }
}