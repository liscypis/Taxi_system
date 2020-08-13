package com.lisowski.clientapp.API

import com.lisowski.clientapp.Constants
import com.lisowski.clientapp.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @POST(Constants.LOGIN_URL)
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST(Constants.REGISTER_URL)
    fun register(@Body request: RegisterRequest): Call<Message>

    @POST(Constants.INITIAL_ORDER)
    fun orderRide(@Header("Authorization") token: String, @Body request: RideRequest): Call<RideDetailResponse>

    @POST(Constants.CONFIRM_ORDER)
    fun confirmRide(@Header("Authorization") token: String, @Body request: ConfirmRequest): Call<Message>
}