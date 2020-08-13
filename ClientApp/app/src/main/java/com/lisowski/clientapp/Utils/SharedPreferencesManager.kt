package com.lisowski.clientapp.Utils

import android.content.Context
import android.content.SharedPreferences
import com.lisowski.clientapp.R

class SharedPreferencesManager(context: Context) {
    private var sharedPref: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    private val USER_ID = "user_id"
    private val USERNAME = "username"
    private val USER_PASSWORD = "user_password"
    private val USER_TOKEN = "user_token"

    /**
     * Function to save user data
     */
    fun saveUserData(userID: Long, username: String, password: String, token: String) {
        val editor = sharedPref.edit()
        editor.putLong(USER_ID, userID)
        editor.putString(USERNAME, username)
        editor.putString(USER_PASSWORD, password)
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return sharedPref.getString(USER_TOKEN, null)
    }
    /**
     * Function to fetch userID
     */
    fun fetchUserId(): Long? {
        return sharedPref.getLong(USER_ID, -1L)
    }
    /**
     * Function to fetch username
     */
    fun fetchUsername(): String? {
        return sharedPref.getString(USERNAME, null)
    }
    /**
     * Function to fetch password
     */
    fun fetchPassword(): String? {
        return sharedPref.getString(USER_PASSWORD, null)
    }
    /**
     * Function to clear SharedPreferences
     */
    fun clear() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}