package com.lisowski.clientapp.models

import kotlin.String

data class LoginResponse(
    var token: String,
    var type: String,
    var id: Int,
    var name: String,
    var surname: String,
    var userName: String,
    var email: String,
    var phoneNum: String,
    var roles: List<String>
)