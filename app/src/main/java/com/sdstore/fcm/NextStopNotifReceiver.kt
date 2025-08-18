package com.sdstore.fcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NextStopNotifReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == "com.sdstore.fcm.ACTION_ACKNOWLEDGE_NEXT_STOP_NOTIF") {
            // Dismiss the notification
            val notificationManager = NotificationManagerCompat.from(context)
            // You will need a notification ID to cancel it.
            // The original code used a static variable, which we will define later.
            // For now, let's assume the ID is 101.
            notificationManager.cancel(101)

            // The original code also cancelled a timer, which we will implement later.
        }
    }
}