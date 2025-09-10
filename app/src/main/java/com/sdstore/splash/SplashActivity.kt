package com.sdstore.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.sdstore.BuildConfig
import com.sdstore.auth.AuthActivity
import com.sdstore.blockemulator.BlockEmulatorActivity
import com.sdstore.core.utils.EmulatorDetector
import com.sdstore.forceupdate.ForceUpdateActivity
import com.sdstore.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        if (EmulatorDetector.isEmulator) {
            val intent = Intent(this, BlockEmulatorActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        checkForceUpdate()
    }

    private fun checkForceUpdate() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(mapOf("minimum_version_code" to 1L))

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val minimumVersionCode = remoteConfig.getLong("minimum_version_code")
                    val currentVersionCode = BuildConfig.VERSION_CODE.toLong()

                    if (currentVersionCode < minimumVersionCode) {
                        navigateTo(ForceUpdateActivity::class.java)
                    } else {
                        checkUserAndNavigate()
                    }
                } else {
                    checkUserAndNavigate()
                }
            }
    }

    private fun checkUserAndNavigate() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            navigateTo(AuthActivity::class.java)
        } else {
            navigateTo(MainActivity::class.java)
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}