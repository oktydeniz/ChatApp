package com.oktydeniz.chatapp.opening.opening.views.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }
    private fun init() {
        val navController =
            Navigation.findNavController(this@HomeActivity, R.id.fragment_home_activity)
        NavigationUI.setupWithNavController(binding.bottomNavigationViewHome, navController)

    }
}