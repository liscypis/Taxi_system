package com.lisowski.driverapp.models

data class ConfirmRequest(val idRide: Long, val confirm: Boolean, val noApp: Boolean = false) {
}