package com.lisowski.clientapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lisowski.clientapp.API.ApiClient
import com.lisowski.clientapp.R
import com.lisowski.clientapp.Utils.SharedPreferencesManager
import com.lisowski.clientapp.models.LoginRequest
import com.lisowski.clientapp.models.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SharedPreferencesManager
    private lateinit var apiClient: ApiClient
    private var LOGIN_ACTIVITY = "LoginActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiClient = ApiClient()
        sessionManager = SharedPreferencesManager(this)

        apiClient.getApiService().login(LoginRequest("nowy6","haslo"))
            .enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d(LOGIN_ACTIVITY, "onFailure: coś nie pykło ${t.message}" )
                }

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val loginResponse = response.body()

                    if (loginResponse?.token != null) {
                        sessionManager.saveAuthToken(loginResponse.token)
                        Log.d(LOGIN_ACTIVITY, "onResponse: $loginResponse")
                    } else {
                        Log.d(LOGIN_ACTIVITY, "onResponse: Error")
                    }
                }
            })
    }
}