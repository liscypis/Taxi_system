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
import com.lisowski.clientapp.Utils.clearError
import com.lisowski.clientapp.Utils.getApiError
import com.lisowski.clientapp.models.APIError
import com.lisowski.clientapp.models.LoginResponse
import com.lisowski.clientapp.models.Message
import com.lisowski.clientapp.models.RegisterRequest
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var apiClient: ApiClient
    private val REGISTER_ACTIVITY = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        apiClient = ApiClient()
        checkInputs()
    }

    private fun checkInputs() {
        registerBnt.setOnClickListener {
            val login = registerUserNameInput.editText?.text.toString().trim()
            val password = registerPasswordInput.editText?.text.toString().trim()
            val name = registerNameInput.editText?.text.toString().trim()
            val surname = registerSurnameInput.editText?.text.toString().trim()
            val email = registerEmailInput.editText?.text.toString().trim()
            val phoneNumber = registerPhoneInput.editText?.text.toString().trim()

            clearEditTextErrors()

            var error = false
            if (login.isEmpty()) {
                registerUserNameInput.error = "Podaj login"
                registerUserNameInput.requestFocus()
                error = true;
            }

            if (password.isEmpty()) {
                registerPasswordInput.error = "Podaj hasło"
                registerPasswordInput.requestFocus()
                error = true;
            }
            if (password.length < 5) {
                registerPasswordInput.error = "Minimalna długość: 5"
                registerPasswordInput.requestFocus()
                error = true;
            }
            if (name.isEmpty()) {
                registerNameInput.error = "Podaj imię"
                registerNameInput.requestFocus()
                error = true;
            }
            if (surname.isEmpty()) {
                registerSurnameInput.error = "Podaj nazwisko"
                registerSurnameInput.requestFocus()
                error = true;
            }
            if (email.isEmpty()) {
                registerEmailInput.error = "Podaj email"
                registerEmailInput.requestFocus()
                error = true;
            }
            if (phoneNumber.length < 9) {
                registerPhoneInput.error = "Za krótki numer"
                registerPhoneInput.requestFocus()
                error = true;
            }
            if (error)
                return@setOnClickListener

            val request: RegisterRequest = RegisterRequest(
                name = name,
                userName = login,
                surname = surname,
                password = password,
                email = email,
                phoneNum = phoneNumber
            )
            Log.d(REGISTER_ACTIVITY, "checkInputs: request ${request.roles}")
            registerUser(request)
        }

    }

    private fun registerUser(request: RegisterRequest) {
        Log.d(REGISTER_ACTIVITY, "checkInputs: request $request")
        apiClient.getApiService().register(request)
            .enqueue(object : Callback<Message> {
                override fun onFailure(call: Call<Message>, t: Throwable) {
                    Log.d(REGISTER_ACTIVITY, "onFailure: coś nie pykło ${t.message}")
                    Toast.makeText(applicationContext, "Brak połączenia", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(call: Call<Message>, response: Response<Message>) {
                    if (response.isSuccessful) {
                        Log.d(REGISTER_ACTIVITY, "onResponse: ${response.body()}")
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        Toast.makeText(applicationContext, "Utworzono konto!", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        val errorResponse: APIError = getApiError(response as Response<Any>)
                        Log.d(REGISTER_ACTIVITY, "onResponse fail: ${errorResponse.toString()}")
                        checkError(errorResponse.message)
                    }

                }
            })
    }

    private fun checkError(error: String) {
        if (error == "Phone number is already in use")
            registerPhoneInput.error = "Numer jest zajęty"
        if (error == "Username is already in use")
            registerUserNameInput.error = "Login jest zajęty"
        if (error == "Email is already in use")
            registerEmailInput.error = "Email jest zajęty"
    }

    private fun clearEditTextErrors() {
        registerPhoneInput.clearError()
        registerEmailInput.clearError()
        registerSurnameInput.clearError()
        registerNameInput.clearError()
        registerPasswordInput.clearError()
        registerUserNameInput.clearError()
    }

}