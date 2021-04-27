package com.oktydeniz.chatapp.opening.opening.opening

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.opening.opening.views.auth.AuthActivity
import com.oktydeniz.chatapp.opening.opening.views.home.HomeActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        supportActionBar?.hide()

        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                val user = FirebaseAuth.getInstance().currentUser
                val intent: Intent = if (user != null) {
                    Intent(this@WelcomeActivity, HomeActivity::class.java)
                } else {
                    Intent(this@WelcomeActivity, AuthActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        }.start()
    }
}