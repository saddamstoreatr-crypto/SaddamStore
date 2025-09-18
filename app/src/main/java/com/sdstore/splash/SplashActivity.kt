package com.sdstore.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sdstore.R
import com.sdstore.core.data.repository.UserRepository
import com.sdstore.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(2000) // Splash screen ka waqt

            val currentUser = Firebase.auth.currentUser
            if (currentUser != null) {
                // getUserProfile() ko coroutine ke andar call kiya gaya hai (Error Fix #1)
                val user = userRepository.getUserProfile()
                if (user != null && user.name.isNotEmpty() && user.outletName.isNotEmpty()) {
                    navigateToMainActivity()
                } else {
                    navigateToRegisterActivity()
                }
            } else {
                navigateToAuthActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToAuthActivity() {
        val intent = Intent()
        // AuthActivity ka package name theek kiya gaya hai (Error Fix #2)
        intent.setClassName(this, "com.sdstore.feature_auth.AuthActivity")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegisterActivity() {
        val intent = Intent()
        intent.setClassName(this, "com.sdstore.feature_auth.register.RegisterActivity")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}