package com.sdstore.fcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NextStopNotifReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == "com.sdstore.fcm.ACTION_DISMISS_NOTIFICATION") {
            val notificationManager = NotificationManagerCompat.from(context)
            // Note: A static notification ID is used here for simplicity.
            // A more robust implementation might pass the ID via the intent.
            notificationManager.cancel(101)
        }
    }
}