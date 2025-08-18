package com.sdstore.fcm

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sdstore.databinding.ActivityNextStopLockScreenBinding

class NextStopLockScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNextStopLockScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        binding = ActivityNextStopLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.acknowledgeButton.setOnClickListener {
            finish()
        }
    }
}