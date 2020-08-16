package com.lisowski.clientapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lisowski.clientapp.API.ApiClient
import com.lisowski.clientapp.Constants.RIDE_DETAIL
import com.lisowski.clientapp.R
import com.lisowski.clientapp.Utils.SharedPreferencesManager
import com.lisowski.clientapp.Utils.clearError
import com.lisowski.clientapp.Utils.getApiError
import com.lisowski.clientapp.adapters.PlaceArrayAdapter
import com.lisowski.clientapp.models.*
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_order.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class OrderActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var placeAdapter: PlaceArrayAdapter? = null
    private lateinit var mPlacesClient: PlacesClient
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SharedPreferencesManager
    private lateinit var countDownTimer: CountDownTimer
    private val ORDER_ACTIVITY = "OrderActivity"
    private var timeLeftInMs :Long = 15000
    private var rideId: Long = -1
    private val context : Context = this
    private lateinit var details :RideDetailResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        hideCard()
        //toolbar and drawer
        initToolBarAndDrawer()

        apiClient = ApiClient()
        sessionManager = SharedPreferencesManager(this)

        Places.initialize(this, "AIzaSyCbPoPSc5SRZy21nq5I7sfKrc8MJGazcBg")
        mPlacesClient = Places.createClient(this)

        placeAdapter = PlaceArrayAdapter(this, R.layout.layout_item_places, mPlacesClient)
        originAutoCompleteTV.setAdapter(placeAdapter)
        destinationAutoCompleteTV.setAdapter(placeAdapter)

        originAutoCompleteTV.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val place = parent.getItemAtPosition(position)
                originAutoCompleteTV.apply {
                    setText(place.toString())
                    setSelection(originAutoCompleteTV.length())
                }
            }
        destinationAutoCompleteTV.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val place = parent.getItemAtPosition(position)
                destinationAutoCompleteTV.apply {
                    setText(place.toString())
                    setSelection(destinationAutoCompleteTV.length())
                }
            }

        cancelBnt.setOnClickListener {
            confirmRide(rideId, false)
            stopTimer()
        }
        confirmBnt.setOnClickListener {
            confirmRide(rideId, true)
        }

        orderBnt.setOnClickListener {
            clearErrorTV()
            //TODO Do testów
            originAutoCompleteTV.setText("Ludomira Różyckiego 25, Kielce, Polska")
            destinationAutoCompleteTV.setText("Wapiennikowa 45, Kielce, Polska")

            val origin = originAutoCompleteTV.text.toString().trim()
            val destination = destinationAutoCompleteTV.text.toString().trim()
            var error = false
            if (origin.isEmpty()) {
                originTV.error = "Podaj punkt startowy"
                originTV.requestFocus()
                error = true;
            }

            if (destination.isEmpty()) {
                destinationTV.error = "Podaj miejsce podróży"
                destinationTV.requestFocus()
                error = true;
            }
            if (error)
                return@setOnClickListener

            getOrderDetails(RideRequest(sessionManager.fetchUserId()!!, origin, destination))
        }


    }


    private fun initToolBarAndDrawer() {
        setSupportActionBar(findViewById(R.id.toolbar))
        val drawerToggle = ActionBarDrawerToggle(this, drawerLay, R.string.open, R.string.close)
        drawerLay.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigationV.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_order -> {
                Log.d(ORDER_ACTIVITY, "onNavigationItemSelected: 1")
//                val intent = Intent(applicationContext, OrderActivity::class.java)
//                startActivity(intent)
            }
            R.id.nav_history -> {
                Log.d(ORDER_ACTIVITY, "onNavigationItemSelected: 1")
                val intent = Intent(applicationContext, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                Log.d(ORDER_ACTIVITY, "onNavigationItemSelected: 3")
                sessionManager.clear()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLay.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLay.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        if (drawerLay.isDrawerOpen(GravityCompat.START)) {
            drawerLay.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }



    private fun clearErrorTV() {
        originTV.clearError()
        destinationTV.clearError()
    }

    private fun getOrderDetails(request: RideRequest) {

        Log.d(ORDER_ACTIVITY, "checkInputs: request $request")
        apiClient.getApiService()
            .orderRide(token = "Bearer ${sessionManager.fetchAuthToken()}", request = request)
            .enqueue(object : Callback<RideDetailResponse> {
                override fun onFailure(call: Call<RideDetailResponse>, t: Throwable) {
                    Log.d(ORDER_ACTIVITY, "onFailure: coś nie pykło ${t.message}")
                    Toast.makeText(applicationContext, "Brak połączenia", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(
                    call: Call<RideDetailResponse>,
                    response: Response<RideDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(ORDER_ACTIVITY, "onResponse: ${response.body()}")
                        details = response.body()!!

                        orderCard.visibility = View.VISIBLE
                        orderBnt.visibility = View.INVISIBLE
                        rideId = details.idRide

                        priceTV.text = details.approxPrice
                        timeTV.text = getTimeFromResponse(details)

                        timeLeftInMs = 15000
                        startTimer()

                    } else {
                        val errorResponse: APIError = getApiError(response as Response<Any>)
                        Log.d(ORDER_ACTIVITY, "onResponse fail: ${errorResponse.toString()}")
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Brak kierowców")
                            .setMessage("Brak dostępnych kierówców, proszę spróbować później")
                            .setPositiveButton("OK") { _, _ ->
                                // Respond to positive button press
                            }
                            .show()
                    }

                }
            })
    }

    private fun getTimeFromResponse(details: RideDetailResponse): String {
        val time: Instant = Instant.now()
        val newTime = time.plus(details.driverDuration + details.userDuration, ChronoUnit.SECONDS)
        Log.d(ORDER_ACTIVITY, "onResponse: $newTime")

        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("HH:mm")
                .withLocale(Locale.GERMAN)
                .withZone(ZoneId.systemDefault())
        return formatter.format(newTime)
    }
    private fun startTimer() {
        countDownTimer = object :CountDownTimer(timeLeftInMs, 1000) {
            override fun onFinish() {
                confirmRide(rideId, false)
                Log.d(ORDER_ACTIVITY, "CountDownTimer: finish ")
            }

            override fun onTick(tick: Long) {
                timeLeftInMs = tick
                updateTimer()
            }

        }.start()
    }

    private fun updateTimer() {
        var sec = timeLeftInMs / 1000
        countdownTV.text = sec.toString()
        Log.d(ORDER_ACTIVITY, "updateTimer: $sec")
    }
    private fun stopTimer() {
        countDownTimer.cancel()
    }

    private fun confirmRide(rideId: Long, conf: Boolean) {
        apiClient.getApiService()
            .confirmRide(token = "Bearer ${sessionManager.fetchAuthToken()}",
                request = ConfirmRequest(idRide = rideId, confirm = conf)
            )
            .enqueue(object : Callback<Message> {
                override fun onFailure(call: Call<Message>, t: Throwable) {
                    Log.d(ORDER_ACTIVITY, "onFailure: coś nie pykło ${t.message}")
                    Toast.makeText(applicationContext, "Brak połączenia", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(call: Call<Message>, response: Response<Message>) {
                    if (response.isSuccessful) {
                        if(conf){
                            stopTimer()
                            val intent = Intent(applicationContext, MapsActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra(RIDE_DETAIL, details)
                            startActivity(intent)

                            Log.d(ORDER_ACTIVITY, "onResponse: ${response.body()}")
                        } else{
                            Log.d(ORDER_ACTIVITY, "onResponse: ${response.body()}")
                            hideCard()
                            orderBnt.visibility = View.VISIBLE
                            Toast.makeText(applicationContext, "Zamowienie anulowane", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        val errorResponse: APIError = getApiError(response as Response<Any>)
                        Log.d(ORDER_ACTIVITY, "onResponse fail: ${errorResponse.toString()}")
                        Toast.makeText(applicationContext, errorResponse.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
    }
    private fun hideCard() {
        orderCard.visibility = View.GONE
    }
}