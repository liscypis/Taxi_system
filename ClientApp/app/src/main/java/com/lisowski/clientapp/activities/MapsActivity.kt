package com.lisowski.clientapp.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.lisowski.clientapp.Constants.RIDE_DETAIL
import com.lisowski.clientapp.R
import com.lisowski.clientapp.models.RideDetailResponse
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_order.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private lateinit var mMap: GoogleMap
    private val MAPS_ACTIVITY: String = "MapsActivity"
    private lateinit var details: RideDetailResponse
    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMs :Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        details = intent.getParcelableExtra(RIDE_DETAIL)!!
        Log.d(MAPS_ACTIVITY, "onCreate: $details")
        timeLeftInMs *= details.driverDuration
        startTimer()
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

        val driverPolyline = googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .addAll(decodePolyline(details.driverPolyline))
                .color(Color.RED)
                .width(5F)
        )
        driverPolyline.tag = "driverPoly"

        googleMap.setOnPolylineClickListener(this)
    }

    private fun decodePolyline(polyline: String): List<LatLng> {
        return PolyUtil.decode(polyline)
    }

    override fun onPolylineClick(poly: Polyline?) {
        if(poly?.width == 5f)
            poly.width = 15f
        else
            poly?.width = 5f
    }

    private fun startTimer() {
        countDownTimer = object :CountDownTimer(timeLeftInMs, 60000) {
            override fun onFinish() {
                Log.d(MAPS_ACTIVITY, "CountDownTimer: finish ")
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