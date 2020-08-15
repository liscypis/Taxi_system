package com.lisowski.clientapp.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lisowski.clientapp.API.ApiClient
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


class HistoryActivity : AppCompatActivity() {
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


        historyRV.adapter = HistoryRecycleAdapter(userHistory)
        historyRV.layoutManager = LinearLayoutManager(this)
        historyRV.setHasFixedSize(true)
    }

}