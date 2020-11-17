package com.example.securefam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.securefam.Activities.MapsActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class BlankFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    fun onClickLogin(v:View){
        var email:String= email.text.toString()
        var password:String= Password.text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(MainActivity.TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(requireContext(), "Authentication Successful.",
                        Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireActivity(), MapsActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(MainActivity.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

            }
    }

    companion object {

    }
}