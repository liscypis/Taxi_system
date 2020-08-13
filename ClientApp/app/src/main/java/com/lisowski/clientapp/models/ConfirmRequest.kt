package com.lisowski.clientapp.models

data class ConfirmRequest(val idRide: Long, val confirm: Boolean, val noApp: Boolean = false) {
}