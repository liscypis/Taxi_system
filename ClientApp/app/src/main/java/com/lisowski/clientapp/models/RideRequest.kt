package com.lisowski.clientapp.models

data class RideRequest(val userId: Long, val origin: String, val destination: String) {
}