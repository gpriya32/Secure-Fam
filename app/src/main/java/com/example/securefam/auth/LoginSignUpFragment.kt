package com.example.securefam.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.securefam.R
import kotlinx.android.synthetic.main.fragment_user_details.*

class LoginSignUpFragment : Fragment() {

    private var btnDownState: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        btnDownState = arguments!!.getBoolean("btnDownState")
        return inflater.inflate(R.layout.fragment_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!btnDownState) {
            tf_first_name.visibility = View.INVISIBLE
            tf_last_name.visibility = View.INVISIBLE
        }
    }
}