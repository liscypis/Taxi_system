package com.lisowski.clientapp.models

import kotlin.String

data class RegisterRequest(
    val name: String,
    val surname: String,
    val userName: String,
    val password: String,
    val email: String,
    val phoneNum: String,
    val roles: Set<String> = setOf("user")
) {

    constructor(request: RegisterRequest) : this(
        name = request.name,
        surname = request.surname,
        userName = request.userName,
        password = request.password,
        email = request.email,
        phoneNum = request.phoneNum
    )
}