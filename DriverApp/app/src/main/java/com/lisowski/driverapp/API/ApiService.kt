package com.lisowski.driverapp.API


import com.lisowski.driverapp.Constants
import com.lisowski.driverapp.models.*
import retrofit2.Call
import retrofit2.http.*
import io.reactivex.Observable

interface ApiService {

    @POST(Constants.LOGIN_URL)
    fun login(@Body request: LoginRequest): Call<LoginResponse>

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

    @GET(Constants.GET_PRICE)
    fun getPriceForRide(
        @Header("Authorization") token: String,
        @Path("id_ride")
        id_ride: Long
    ): Observable<Message>

    @PUT(Constants.COMPLETE_RIDE)
    fun setRidestatus(
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
        @Path("id_driver")
        id_ride: Long
    ): Observable<List<RideDetails>>

    @GET(Constants.CHECK_FOR_NEW_RIDE)
    fun checkForNewRide(
        @Header("Authorization") token: String,
        @Path("id")
        id: Long
    ): Observable<RideDetailResponse>

    @POST(Constants.ADD_LOCATION)
    fun addLocation(
        @Header("Authorization") token: String,
        @Body request: Location
    ): Observable<Message>

    @POST(Constants.DRIVER_STATUS)
    fun setDriverStatus(
        @Header("Authorization") token: String,
        @Body request: StatusMessage
    ): Observable<Message>


}