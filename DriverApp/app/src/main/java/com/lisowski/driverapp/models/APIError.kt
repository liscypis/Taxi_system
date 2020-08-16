package com.lisowski.driverapp.models

import kotlin.String

data class APIError(
    val timestamp: String,
    val status: String,
    val error: String,
    val message: String,
    val path: String
)