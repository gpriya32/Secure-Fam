package com.example.securefam.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.securefam.home.MapsActivity
import com.example.securefam.R
import com.example.securefam.app.SecureFamApp.Companion.sharedPrefs
import com.example.securefam.util.AppDialog
import com.example.securefam.util.GlobalUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_onboard.*
import kotlinx.android.synthetic.main.fragment_user_details.*

class OnboardActivity : AppCompatActivity(), View.OnClickListener {

    private var btnDownState = false;
    private lateinit var auth: FirebaseAuth
    private val loadingDialog = AppDialog.instance()

    companion object {
        var TAG ="MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard)

        btn_up.text = getString(R.string.login)
        btn_down.text = getString(R.string.sign_up) + "?"
        openFragment(LoginSignUpFragment(), btnDownState)

        auth = FirebaseAuth.getInstance()

        btn_down.setOnClickListener {onClick(btn_down)}
        btn_up.setOnClickListener {onClick(btn_up)}
    }

    private fun openFragment(fragment: Fragment, btnDownState: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        fragment.arguments = Bundle().apply {
            putBoolean("btnDownState", btnDownState)
        }
        transaction.replace(R.id.fragment_user_details, fragment, fragment.tag)
        transaction.commit()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_up -> {
                if(btnDownState) {
                    onClickFirebaseSignUp()
                } else {
                    onClickFirebaseLogin()
                }
            }
            R.id.btn_down -> {
                if(btnDownState) {
                    btn_down.text = getString(R.string.sign_up) + "?"
                    btn_up.text = getString(R.string.login)
                    openFragment(LoginSignUpFragment(), btnDownState)
                } else {
                    btn_down.text= getString(R.string.login) + "?"
                    btn_up.text = getString(R.string.sign_up)
                    openFragment(LoginSignUpFragment(), btnDownState)
                }
                btnDownState = !btnDownState
            }
        }
    }

    private fun onClickFirebaseLogin() {
        loadingDialog.show(supportFragmentManager, loadingDialog.tag)

        auth.currentUser?.let {
            sharedPrefs?.clearSession()
            auth.signOut()
        }

        var currFragment = supportFragmentManager.findFragmentByTag(getString(R.string.fragment_user_details))!!
        auth.signInWithEmailAndPassword(currFragment.tf_email.text.toString(), currFragment.tf_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    loadingDialog.dismiss()
                    auth.currentUser?.let {
                        GlobalUtils.startActivityAsNewStack(Intent(this, MapsActivity::class.java), this)
                        finish()
                    }
                } else {
                    loadingDialog.dismiss()
                    //TODO: check any firebase error for wrong credential and show valid message
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun onClickFirebaseSignUp() {
        loadingDialog.show(supportFragmentManager, loadingDialog.tag)

        auth.currentUser?.let {
            sharedPrefs?.clearSession()
            auth.signOut()
        }

        var currFragment = supportFragmentManager.findFragmentByTag(getString(R.string.fragment_user_details))!!
        auth.signInWithEmailAndPassword(currFragment.tf_email.text.toString(), currFragment.tf_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    loadingDialog.dismiss()
                    auth.currentUser?.let{
                        btn_down.text = getString(R.string.sign_up) + "?"
                        btn_up.text = getString(R.string.login)
                        openFragment(LoginSignUpFragment(), btnDownState)
                        btnDownState = false
                    }
                } else {
                    loadingDialog.dismiss()
                    //TODO : check firebase auth exception for email signup and show valid message
                    Toast.makeText(this, "Sign Up failed. Try again!", Toast.LENGTH_LONG).show()
                }
            }
    }
}


