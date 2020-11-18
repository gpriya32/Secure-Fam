package com.example.securefam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.securefam.app.SecureFamApp.Companion.sharedPrefs
import com.example.securefam.auth.OnboardActivity
import com.example.securefam.home.MapsActivity
import com.example.securefam.util.GlobalUtils
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if(FirebaseAuth.getInstance().currentUser?.uid != null && sharedPrefs?.userName != null ) {
                GlobalUtils.startActivityAsNewStack(Intent(this, MapsActivity::class.java), this)
                finish()
            } else {
                GlobalUtils.startActivityAsNewStack(Intent(this, OnboardActivity::class.java), this)
                finish()
            }
        }, 3000)
    }
}