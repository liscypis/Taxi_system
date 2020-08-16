package com.lisowski.driverapp.models

data class RideRequest(val userId: Long, val origin: String, val destination: String) {
}