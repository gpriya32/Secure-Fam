package com.example.securefam.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.securefam.app.SecureFamApp.Companion.sharedPrefs
import com.google.firebase.auth.FirebaseAuth

class GlobalUtils {
    companion object {

        fun startActivityAsNewStack(
            intent: Intent,
            context: Context
        ) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }

        fun logout(
            context: Context,
            activity: Activity?
        ) {
            sharedPrefs?.clearSession()
            FirebaseAuth.getInstance().signOut()
        }
    }
}