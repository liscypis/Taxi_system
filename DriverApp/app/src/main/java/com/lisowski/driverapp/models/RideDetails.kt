package com.lisowski.driverapp.models

import java.time.Instant

data class RideDetails(
    val idRide: Long,
    val idUser: Long,
    val idDriver: Long,
    val userDistance: Long,
    val driverDistance: Long,
    val timeStart: String,
    val arrivalTime: String,
    val endTime: String,
    val userPolyline: String,
    val driverPolyline: String,
    val userLocation: String,
    val userDestination: String,
    val rating: Int,
    val price: Float
) {
}