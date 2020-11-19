package com.example.securefam.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import androidx.work.WorkManager
import com.example.securefam.SplashActivity
import com.example.securefam.app.SecureFamApp.Companion.sharedPrefs
import com.example.securefam.worker.DatabaseUpdateWorker
import com.google.firebase.auth.FirebaseAuth


class GlobalUtils {
    companion object {

        private val DATABASE_UPDATE_WORKER = DatabaseUpdateWorker::class.java.simpleName

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

            activity?.let {
                try {
                    // Stopping Session Ping Worker
                    WorkManager.getInstance(activity.applicationContext)
                        .cancelUniqueWork(DATABASE_UPDATE_WORKER)
                } catch (e: Exception) {
                    //Log.wtf("Application Logout", "Unable to close services.")
                }
            }

            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }

        fun validateDetails(vararg input: String?): Boolean {
            for (item in input) {
                if (item == null) return false
            }
            return true
        }

        fun isNetworkAvailable(context: Context): Boolean {
            var isConnected = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }

        fun getFirebaseErrorMessage(errorCode: String?, context: Context) {
            when (errorCode) {
                "ERROR_INVALID_CUSTOM_TOKEN" -> Toast.makeText(
                    context,
                    "The custom token format is incorrect. Please check the documentation.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_CUSTOM_TOKEN_MISMATCH" -> Toast.makeText(
                    context,
                    "The custom token corresponds to a different audience.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_INVALID_CREDENTIAL" -> Toast.makeText(
                    context,
                    "The supplied auth credential is malformed or has expired.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_INVALID_EMAIL" -> {
                    Toast.makeText(
                        context,
                        "The email address is badly formatted.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                "ERROR_WRONG_PASSWORD" -> {
                    Toast.makeText(
                        context,
                        "The password is invalid or the user does not have a password.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                "ERROR_USER_MISMATCH" -> Toast.makeText(
                    context,
                    "The supplied credentials do not correspond to the previously signed in user.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_REQUIRES_RECENT_LOGIN" -> Toast.makeText(
                    context,
                    "This operation is sensitive and requires recent authentication. Log in again before retrying this request.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> Toast.makeText(
                    context,
                    "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                    Toast.makeText(
                        context,
                        "The email address is already in use by another account.   ",
                        Toast.LENGTH_LONG
                    ).show()
                }
                "ERROR_CREDENTIAL_ALREADY_IN_USE" -> Toast.makeText(
                    context,
                    "This credential is already associated with a different user account.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_USER_DISABLED" -> Toast.makeText(
                    context,
                    "The user account has been disabled by an administrator.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_USER_TOKEN_EXPIRED" -> Toast.makeText(
                    context,
                    "The user\\'s credential is no longer valid. The user must sign in again.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_USER_NOT_FOUND" -> Toast.makeText(
                    context,
                    "There is no user record corresponding to this identifier. The user may have been deleted.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_INVALID_USER_TOKEN" -> Toast.makeText(
                    context,
                    "The user\\'s credential is no longer valid. The user must sign in again.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_OPERATION_NOT_ALLOWED" -> Toast.makeText(
                    context,
                    "This operation is not allowed. You must enable this service in the console.",
                    Toast.LENGTH_LONG
                ).show()
                "ERROR_WEAK_PASSWORD" -> {
                    Toast.makeText(
                        context,
                        "The given password is invalid.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}