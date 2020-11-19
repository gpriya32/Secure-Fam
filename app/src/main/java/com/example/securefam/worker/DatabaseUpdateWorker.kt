package com.example.securefam.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.securefam.app.SecureFamApp.Companion.sharedPrefs
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DatabaseUpdateWorker(appContext: Context, workerParameters: WorkerParameters) :
    Worker(appContext, workerParameters) {
    private lateinit var database: DatabaseReference
    override fun doWork(): Result {
        database = Firebase.database.reference
        sharedPrefs?.uId?.let {
            Log.wtf(TAG, sharedPrefs?.userLat)
            Log.wtf(TAG, sharedPrefs?.userLong)
            database.child("users/${it}/user_lat").setValue(sharedPrefs?.userLat) { error, ref ->
                error.let { Result.failure() }
            }
            database.child("users/${it}/user_long").setValue(sharedPrefs?.userLong) { error, ref ->
                error.let { Result.failure() }
            }
        }
        return Result.success()
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}