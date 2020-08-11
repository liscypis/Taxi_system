package com.lisowski.clientapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lisowski.clientapp.API.ApiClient
import com.lisowski.clientapp.R
import com.lisowski.clientapp.Utils.SharedPreferencesManager
import com.lisowski.clientapp.models.APIError
import com.lisowski.clientapp.models.LoginRequest
import com.lisowski.clientapp.models.LoginResponse
import kotlinx.android.synthetic.main.activity_main.*
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

        loginBnt.setOnClickListener {

            val login = loginInput.editText?.text.toString().trim()
            val password = passwordInput.editText?.text.toString().trim()

            if (login.isEmpty()) {
                loginInput.error = "Podaj login"
                loginInput.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordInput.error = "Podaj hasło"
                passwordInput.requestFocus()
                return@setOnClickListener
            }

            apiClient.getApiService().login(LoginRequest(login, password))
                .enqueue(object : Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.d(LOGIN_ACTIVITY, "onFailure: coś nie pykło ${t.message}")
                    }

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse?.token != null) {
                                sessionManager.saveAuthToken(loginResponse.token)
                                Log.d(
                                    LOGIN_ACTIVITY,
                                    "onResponse success: ${loginResponse.toString()}"
                                )
                            }
                        } else {
                            val gson = Gson()
                            val type = object : TypeToken<APIError>() {}.type
                            val errorResponse: APIError =
                                gson.fromJson(response.errorBody()?.charStream(), type)
                            Log.d(LOGIN_ACTIVITY, "onResponse fail: ${errorResponse.toString()}")
                            if (errorResponse.message == "Unauthorized")
                                showLoginError()
                        }

                    }
                })
        }

    }

    private fun showLoginError() {
        Toast.makeText(applicationContext, "Błędne dane", Toast.LENGTH_LONG)
            .show()
        errorTV.text = "Błędne dane"
        passwordInput.error = " "
        passwordInput.requestFocus()
        loginInput.error = " "
        loginInput.requestFocus()
    }
}