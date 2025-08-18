package com.sdstore.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sdstore.R

class MessagingService : FirebaseMessagingService() {

    /**
     * यह فنکشن تب کال ہوتا ہے جب Firebase اس ڈیوائس کے لیے ایک نیا یا اپ ڈیٹ شدہ ٹوکن بناتا ہے۔
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: اس ٹوکن کو اپنے سرور پر بھیجیں تاکہ آپ اس ڈیوائس کو نوٹیفیکیشن بھیج سکیں
    }

    /**
     * यह فنکشن تب کال ہوتا ہے جب ایپ کو کوئی پش نوٹیفیکیشن موصول ہوتا ہے۔
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
        val body = message.notification?.body

        if (title != null && body != null) {
            showNotification(title, body)
        }
    }

    /**
     * यह فنکشن نوٹیفکیشن کو بناتا اور دکھاتا ہے۔
     */
    private fun showNotification(title: String, message: String) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"

        // Android 8.0 (Oreo) اور اس سے اوپر کے ورژنز کے لیے نوٹیفکیشن چینل بنانا ضروری ہے
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_cart_24dp) // یہاں آپ اپنا ایپ آئیکن استعمال کر سکتے ہیں
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // صارف کے کلک کرنے پر نوٹیفکیشن خود بخود ہٹ جائے

        // نوٹیفکیشن دکھائیں
        NotificationManagerCompat.from(this).notify(1, builder.build())
    }
}