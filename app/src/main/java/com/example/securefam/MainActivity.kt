package com.example.securefam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.securefam.Activities.MapsActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var btnDownState = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_down.setOnClickListener {onClick(btn_down)}
        btn_up.setOnClickListener {onClick(btn_up)}
        btn_down.text = getString(R.string.sign_up) + "?"
        FirebaseApp.initializeApp(applicationContext)
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_user_details, fragment, fragment.tag)
        transaction.commit()
    }

    companion object {
        var TAG ="MainActivity"
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_up -> {
                openFragment()
            }
            R.id.btn_down -> {
                if(btnDownState) {

                } else {

                }
            }
        }
    }
}


