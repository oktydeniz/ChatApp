package com.oktydeniz.chatapp.opening.opening.views.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oktydeniz.chatapp.R

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide()
    }

}