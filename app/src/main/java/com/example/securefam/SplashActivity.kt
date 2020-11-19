package com.example.securefam

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.securefam.app.SecureFamApp.Companion.sharedPrefs
import com.example.securefam.ui.auth.OnboardActivity
import com.example.securefam.ui.home.MapsActivity
import com.example.securefam.util.GlobalUtils
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Log.wtf("Splash", sharedPrefs?.userName)

        Handler().postDelayed({
            if (FirebaseAuth.getInstance().currentUser?.uid != null && sharedPrefs?.userName != null) {
                GlobalUtils.startActivityAsNewStack(Intent(this, MapsActivity::class.java), this)
                finish()
            } else {
                GlobalUtils.startActivityAsNewStack(Intent(this, OnboardActivity::class.java), this)
                finish()
            }
        }, 1000)
    }
}