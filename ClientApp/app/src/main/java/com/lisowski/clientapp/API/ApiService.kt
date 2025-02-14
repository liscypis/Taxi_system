package com.lisowski.clientapp.API

import com.lisowski.clientapp.Constants
import com.lisowski.clientapp.models.*
import retrofit2.Call
import retrofit2.http.*
import io.reactivex.Observable

interface ApiService {

    @POST(Constants.LOGIN_URL)
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST(Constants.REGISTER_URL)
    fun register(@Body request: RegisterRequest): Call<Message>

    @POST(Constants.INITIAL_ORDER)
    fun orderRide(
        @Header("Authorization") token: String,
        @Body request: RideRequest
    ): Call<RideDetailResponse>

    @POST(Constants.CONFIRM_ORDER)
    fun confirmRide(
        @Header("Authorization") token: String,
        @Body request: ConfirmRequest
    ): Call<Message>

    @GET(Constants.GET_POSITION)
    fun getDriverLoc(
        @Header("Authorization") token: String,
        @Path("driverID") driverID: Long
    ): Observable<Message>

    @GET(Constants.GET_RIDE_STATUS)
    fun getRideStatus(
        @Header("Authorization") token: String,
        @Path("id")
        rideId: Long
    ): Observable<Message>

    @PUT(Constants.CONFIRM_DRIVER_ARRIVE)
    fun confirmDriverArrive(
        @Header("Authorization") token: String,
        @Path("id")
        rideId: Long
    ): Observable<Message>

    @GET(Constants.GET_DRIVER_CAR)
    fun getDriverCar(
        @Header("Authorization") token: String,
        @Path("id")
        rideId: Long
    ): Observable<Car>

    @GET(Constants.GET_PRICE)
    fun getPriceForRide(
        @Header("Authorization") token: String,
        @Path("id_ride")
        id_ride: Long
    ): Observable<Message>

    @PUT(Constants.COMPLETE_RIDE)
    fun setRideToComplete(
        @Header("Authorization") token: String,
        @Body request: StatusMessage
    ): Observable<Message>

    @PUT(Constants.RIDE_RATE)
    fun setRideRating(
        @Header("Authorization") token: String,
        @Body request: RideRating
    ): Observable<Message>

    @GET(Constants.GET_HISTORY)
    fun getHistory(
        @Header("Authorization") token: String,
        @Path("id_user")
        id_ride: Long
    ): Observable<List<RideDetails>>
}