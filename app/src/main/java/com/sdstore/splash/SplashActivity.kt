package com.sdstore.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sdstore.auth.AuthActivity
import com.sdstore.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Yeh line super.onCreate() se pehle likhni zaroori hai
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Is activity ke liye ab layout file (setContentView) ki zaroorat nahi hai.
        // Theme khud icon aur background dikhayega.

        // Yahan check karne ka logic aayega ke user logged in hai ya nahi.
        // Abhi ke liye, hum farz karte hain ke user logged in nahi hai.
        val isLoggedIn = false // Isko baad mein asli logic se replace kareinge

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish() // Splash activity ko khatam kar dein
    }
}