package com.example.securefam.app

import android.app.Application
import com.google.firebase.FirebaseApp

class SecureFamApp: Application() {

    companion object {
        var sharedPrefs: SharedPrefs? = null
    }

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = SharedPrefs(applicationContext)
        FirebaseApp.initializeApp(applicationContext)
    }
}