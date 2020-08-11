package com.lisowski.clientapp.API

import com.lisowski.clientapp.Constants
import com.lisowski.clientapp.models.LoginRequest
import com.lisowski.clientapp.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @POST(Constants.LOGIN_URL)
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}