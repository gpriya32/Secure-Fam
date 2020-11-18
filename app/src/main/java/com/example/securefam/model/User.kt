package com.example.securefam.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var email: String?,
    var first_name: String?,
    var last_name: String?,
    var user_lat: String?,
    var user_long: String?
)