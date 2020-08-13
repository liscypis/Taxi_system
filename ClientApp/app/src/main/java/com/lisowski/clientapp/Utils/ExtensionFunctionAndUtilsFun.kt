package com.lisowski.clientapp.Utils

import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lisowski.clientapp.models.APIError
import com.lisowski.clientapp.models.Message
import retrofit2.Response

fun TextInputLayout.clearError() {
    error = null
}

fun getApiError(response: Response<Any>): APIError {
    val gson = Gson()
    val type = object : TypeToken<APIError>() {}.type
    return gson.fromJson(response.errorBody()?.charStream(), type)
}