package com.sdstore

import android.app.Application
import android.content.res.Configuration
import java.util.Locale

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Set default language to Urdu for the entire app
        setAppLocale()

        // یہاں دیگر لائبریریز (जैसे Firebase, Algolia, etc.) کو initialize کیا جائے گا
        // ابھی کے لیے یہ خالی ہے
    }

    private fun setAppLocale() {
        val locale = Locale("ur")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        config.setLayoutDirection(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}