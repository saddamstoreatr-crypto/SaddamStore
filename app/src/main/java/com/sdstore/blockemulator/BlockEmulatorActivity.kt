package com.sdstore.blockemulator

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.sdstore.R

class BlockEmulatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_emulator)

        val okButton: Button = findViewById(R.id.okayButton)
        okButton.setOnClickListener {
            finish()
        }
    }
}