package com.lisowski.clientapp.models

data class APIError(
    val timestamp: String,
    val status: String,
    val error: String,
    val message: String,
    val path: String
)