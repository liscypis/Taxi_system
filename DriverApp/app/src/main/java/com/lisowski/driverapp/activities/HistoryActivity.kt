package com.lisowski.driverapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.lisowski.clientapp.API.ApiClient
import com.lisowski.driverapp.Utils.SharedPreferencesManager
import com.lisowski.driverapp.adapters.HistoryRecycleAdapter
import com.lisowski.driverapp.models.RideDetails
import com.lisowski.driverapp.Constants
import com.lisowski.driverapp.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history.*
import java.util.ArrayList

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
            getDriverHistory(id)
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
        val driverPolyline :String = userHistory[position].driverPolyline

        val intent = Intent(applicationContext, MapActivity::class.java)
        intent.putExtra(Constants.USER_LOC, userLocation)
        intent.putExtra(Constants.USER_DEST, userDestination)
        intent.putExtra(Constants.USER_POLYLINE, userPolyline)
        intent.putExtra(Constants.DRIVER_POLYLINE, driverPolyline)
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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

    private fun getDriverHistory(id: Long) {
        Log.d(HISTORY_ACTIVITY, "getUserHistory: ideee $id")
        val observable = apiClient.getApiService()
            .getHistory(
                token = "Bearer ${sessionManager.fetchAuthToken()}", id_ride = id)
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