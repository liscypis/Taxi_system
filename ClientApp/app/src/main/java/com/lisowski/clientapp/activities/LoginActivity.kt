package com.lisowski.clientapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lisowski.clientapp.API.ApiClient
import com.lisowski.clientapp.R
import com.lisowski.clientapp.Utils.SharedPreferencesManager
import com.lisowski.clientapp.Utils.clearError
import com.lisowski.clientapp.Utils.getApiError
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
    private lateinit var userPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiClient = ApiClient()
        sessionManager = SharedPreferencesManager(this)

        loginWithSavedData()

        noAccountTV.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        loginBnt.setOnClickListener {
            clearEditTextErrors()
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
            userPassword = password
            loginToSystem(login, userPassword)
        }

    }

    /**
     *  function check data from SharedPreferences. If there is data, try to log in
     */
    private fun loginWithSavedData() {
        val userLogin = sessionManager.fetchUsername()
        val userPass = sessionManager.fetchPassword()
        if (userLogin != null || userPass != null) {
            userPassword = userPass!!
            loginToSystem(userLogin!!, userPassword)
        }

    }


    private fun loginToSystem(login: String, password: String) {
        Log.d(LOGIN_ACTIVITY, "dane logowanie $login, $password")
        apiClient.getApiService().login(LoginRequest(login, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d(LOGIN_ACTIVITY, "onFailure: coś nie pykło ${t.message}")
                    Toast.makeText(applicationContext, "Brak połączenia", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(
                    call: Call<LoginResponse>, response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        checkSuccessResponse(loginResponse)
                        val intent = Intent(applicationContext, OrderActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        getErrorResponse(response)
                    }

                }
            })
    }

    private fun getErrorResponse(response: Response<LoginResponse>) {
        val errorResponse: APIError = getApiError(response as Response<Any>)
        Log.d(LOGIN_ACTIVITY, "onResponse fail: ${errorResponse.toString()}")
        if (errorResponse.message == "Unauthorized")
            showLoginError()
    }

    private fun checkSuccessResponse(loginResponse: LoginResponse?) {
        if (loginResponse!!.roles.contains("ROLE_USER")) {
            sessionManager.saveUserData(
                password = userPassword,
                username = loginResponse.userName,
                userID = loginResponse.id,
                token = loginResponse.token
            )
            Log.d(LOGIN_ACTIVITY, "onResponse success: ${loginResponse.toString()}")
        } else {
            Toast.makeText(applicationContext, "Brak uprawnień", Toast.LENGTH_LONG).show()
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

    private fun clearEditTextErrors() {
        passwordInput.clearError()
        loginInput.clearError()
    }
}