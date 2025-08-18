package com.sdstore.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.sdstore.R
import com.sdstore.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // اگر یہ ایکٹیویٹی رجسٹریشن کے عمل کے دوران کھولی گئی ہے تو
        // براہ راست فون نمبر والے صفحے پر جائیں
        if (intent.getBooleanExtra("showPhonePageInRegisterProcess", false)) {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment
            val navController = navHostFragment.navController
            val navGraph = navController.navInflater.inflate(R.navigation.nav_auth)
            navGraph.setStartDestination(R.id.enterPhoneFragment)
            navController.graph = navGraph
        }
    }
}