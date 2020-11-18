package com.example.securefam.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.securefam.R
import com.example.securefam.app.SecureFamApp.Companion.sharedPrefs
import com.example.securefam.model.User
import com.example.securefam.ui.home.MapsActivity
import com.example.securefam.util.AppDialog
import com.example.securefam.util.GlobalUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_onboard.*
import kotlinx.android.synthetic.main.fragment_user_details.*


class OnboardActivity : AppCompatActivity(), View.OnClickListener {

    private var btnDownState = false
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val loadingDialog = AppDialog.instance()

    companion object {
        var TAG ="MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard)

        btn_up.text = getString(R.string.login)
        btn_down.text = "${getString(R.string.sign_up)} ?"
        openFragment(LoginSignUpFragment(), btnDownState)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        btn_down.setOnClickListener {onClick(btn_down)}
        btn_up.setOnClickListener {onClick(btn_up)}
    }

    private fun openFragment(fragment: Fragment, btnDownState: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        fragment.arguments = Bundle().apply {
            putBoolean("btnDownState", btnDownState)
        }
        transaction.replace(R.id.details_frame, fragment, fragment.tag)
        transaction.commit()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_up -> {
                if (btnDownState) {
                    onClickFirebaseSignUp()
                } else {
                    onClickFirebaseLogin()
                }
            }
            R.id.btn_down -> {
                btnDownState = !btnDownState
                if (!btnDownState) {
                    btn_down.text = getString(R.string.sign_up) + "?"
                    btn_up.text = getString(R.string.login)
                    openFragment(LoginSignUpFragment(), btnDownState)
                } else {
                    btn_down.text = getString(R.string.login) + "?"
                    btn_up.text = getString(R.string.sign_up)
                    openFragment(LoginSignUpFragment(), btnDownState)
                }
            }
        }
    }

    private fun onClickFirebaseLogin() {
        if (!GlobalUtils.isNetworkAvailable(applicationContext)) {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_LONG).show()
            return
        }

        loadingDialog.show(supportFragmentManager, loadingDialog.tag)

        auth.currentUser?.let {
            sharedPrefs?.clearSession()
            auth.signOut()
        }

        var currFragment =
            supportFragmentManager.findFragmentById(R.id.details_frame)!!
        auth.signInWithEmailAndPassword(
            currFragment.tf_email.text.toString(),
            currFragment.tf_password.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let {
                        database.child("users/${it.uid}").apply {
                            addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    sharedPrefs?.userName =
                                        "${dataSnapshot.child("first_name").value.toString()} ${
                                            dataSnapshot.child("last_name").value.toString()
                                        }"
                                    sharedPrefs?.userLat =
                                        dataSnapshot.child("user_lat").value.toString()
                                    sharedPrefs?.userLong =
                                        dataSnapshot.child("user_long").value.toString()
                                    loadingDialog.dismiss()
                                    GlobalUtils.startActivityAsNewStack(
                                        Intent(
                                            baseContext,
                                            MapsActivity::class.java
                                        ), baseContext
                                    )
                                    finish()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        baseContext,
                                        "Some error occur. Try again!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                        }
                    }
                } else {
                    loadingDialog.dismiss()
                    GlobalUtils.getFirebaseErrorMessage((task.exception as FirebaseAuthException).errorCode, baseContext)
                }
            }
    }

    private fun onClickFirebaseSignUp() {
        if (!GlobalUtils.isNetworkAvailable(applicationContext)) {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_LONG).show()
            return
        }

        loadingDialog.show(supportFragmentManager, loadingDialog.tag)

        auth.currentUser?.let {
            sharedPrefs?.clearSession()
            auth.signOut()
        }

        var currFragment =
            supportFragmentManager.findFragmentById(R.id.details_frame)!!

        auth.createUserWithEmailAndPassword(
            currFragment.tf_email.text.toString(),
            currFragment.tf_password.text.toString()
        )
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    auth.currentUser?.let {
                        database.child("users/${it.uid}").setValue(
                            User(
                                currFragment.tf_email.text.toString(),
                                currFragment.tf_first_name.text.toString(),
                                currFragment.tf_last_name.text.toString(),
                                null,
                                null
                            )
                        ) { error, ref ->
                            if (error == null) {
                                btnDownState = false
                                btn_down.text = getString(R.string.sign_up) + "?"
                                btn_up.text = getString(R.string.login)
                                openFragment(LoginSignUpFragment(), btnDownState)
                                loadingDialog.dismiss()
                                Toast.makeText(
                                    baseContext,
                                    "Sign Up successful!",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "Sign Up failed. Try again!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                } else {
                    loadingDialog.dismiss()
                    GlobalUtils.getFirebaseErrorMessage((task.exception as FirebaseAuthException).errorCode, this)
                }
            }
    }
}


