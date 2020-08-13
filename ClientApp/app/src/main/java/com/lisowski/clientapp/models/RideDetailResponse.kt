package com.lisowski.clientapp.models

data class RideDetailResponse(
    val idRide: Long,
    val idDriver: Long,
    val userDistance: Long,
    val driverDistance: Long,
    val userDuration: Long,
    val driverDuration: Long,
    val userPolyline: String,
    val driverPolyline: String,
    val userLocation: String,
    val userDestination: String,
    val userPhone: String,
    val driverPhone: String,
    val approxPrice: String
) {
}