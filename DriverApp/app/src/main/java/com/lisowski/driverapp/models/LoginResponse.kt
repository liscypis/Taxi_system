package com.lisowski.driverapp.models

import kotlin.String

data class LoginResponse(
    var token: String,
    var type: String,
    var id: Long,
    var name: String,
    var surname: String,
    var userName: String,
    var email: String,
    var phoneNum: String,
    var roles: List<String>
)