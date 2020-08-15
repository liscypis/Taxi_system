package com.lisowski.clientapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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


class HistoryActivity : AppCompatActivity(), HistoryRecycleAdapter.OnItemClickListener {
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SharedPreferencesManager
    private lateinit var userHistory: ArrayList<RideDetails>
    private val HISTORY_ACTIVITY = "HistoryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)


        apiClient = ApiClient()
        sessionManager = SharedPreferencesManager(this)

        val id = sessionManager.fetchUserId()
        if (id!! > 0) {
            getUserHistory(id)
        }
    }

    private fun getUserHistory(id: Long) {
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
    }

    private fun onResponse(response: List<RideDetails>?) {
        userHistory = response as ArrayList<RideDetails>
        Log.d(HISTORY_ACTIVITY, "onResponse: size ${userHistory.size}")


        historyRV.adapter = HistoryRecycleAdapter(userHistory,this)
        historyRV.layoutManager = LinearLayoutManager(this)
        historyRV.setHasFixedSize(true)
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

}