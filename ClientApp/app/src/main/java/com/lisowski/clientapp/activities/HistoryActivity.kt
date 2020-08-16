package com.lisowski.clientapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.lisowski.clientapp.API.ApiClient
import com.lisowski.clientapp.Constants
import com.lisowski.clientapp.R
import com.lisowski.clientapp.Utils.SharedPreferencesManager
import com.lisowski.clientapp.adapters.HistoryRecycleAdapter
import com.lisowski.clientapp.models.RideDetails
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history.*
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


class HistoryActivity : AppCompatActivity(), HistoryRecycleAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SharedPreferencesManager
    private lateinit var userHistory: ArrayList<RideDetails>
    private val HISTORY_ACTIVITY = "HistoryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)


        //drawerConf
        initToolBarAndDrawer()


        apiClient = ApiClient()
        sessionManager = SharedPreferencesManager(this)

        val id = sessionManager.fetchUserId()
        if (id!! > 0) {
            getUserHistory(id)
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

    override fun onItemClick(position: Int) {
        val userLocation: String = userHistory[position].userLocation
        val userDestination :String = userHistory[position].userDestination
        val userPolyline :String = userHistory[position].userPolyline

        val intent = Intent(applicationContext, MapsActivity::class.java)
        intent.putExtra(Constants.USER_LOC, userLocation)
        intent.putExtra(Constants.USER_DEST, userDestination)
        intent.putExtra(Constants.USER_POLYLINE, userPolyline)
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_order -> {
                Log.d(HISTORY_ACTIVITY, "onNavigationItemSelected: 1")
                val intent = Intent(applicationContext, OrderActivity::class.java)
                startActivity(intent)
        }
            R.id.nav_history -> {
                Log.d(HISTORY_ACTIVITY, "onNavigationItemSelected: 1")
//                val intent = Intent(applicationContext, HistoryActivity::class.java)
//                startActivity(intent)
        }
            R.id.nav_logout -> {
                Log.d(HISTORY_ACTIVITY, "onNavigationItemSelected: 3")
                sessionManager.clear()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
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

    private fun getUserHistory(id: Long) {
        Log.d(HISTORY_ACTIVITY, "getUserHistory: ideee $id")
        val observable = apiClient.getApiService()
            .getHistory(
                token = "Bearer ${sessionManager.fetchAuthToken()}", id_ride = 10)
        val subscribe = observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
    }

    private fun onFailure(t: Throwable?) {
        Log.d(HISTORY_ACTIVITY, "onFailure: ${t!!.message}")
        showDialog()
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Brak historii")
            .setMessage("Pora odbyć swój pierwszy kurs")
            .setPositiveButton("OK") { _, _ ->
                // Respond to positive button press
            }
            .show()
    }

    private fun onResponse(response: List<RideDetails>?) {
        userHistory = response as ArrayList<RideDetails>
        Log.d(HISTORY_ACTIVITY, "onResponse: size ${userHistory.size}")


        historyRV.adapter = HistoryRecycleAdapter(userHistory,this)
        historyRV.layoutManager = LinearLayoutManager(this)
        historyRV.setHasFixedSize(true)
    }




}