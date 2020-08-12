package com.lisowski.clientapp.models

import kotlin.String

data class APIError(
    val timestamp: String,
    val status: String,
    val error: String,
    val message: String,
    val path: String
)