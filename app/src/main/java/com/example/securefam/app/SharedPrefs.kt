package com.example.securefam.app

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPrefs(context: Context) {
    private val instance: SharedPreferences = context.getSharedPreferences("${context.packageName}_preferences", MODE_PRIVATE)

    private val user_name = "user_name"
    var userName: String?
        get() = instance.getString(user_name, null)
        set(value) = instance.edit().putString(user_name, value).apply()

    private val user_lat = "user_lat"
    var userLat: String?
        get() = instance.getString(user_lat, null)
        set(value) = instance.edit().putString(user_lat, value).apply()

    private val user_long = "user_long"
    var userLong: String?
        get() = instance.getString(user_long, null)
        set(value) = instance.edit().putString(user_long, value).apply()

    fun clearSession() {
        instance.edit()
            .clear()
            .apply()
    }
}