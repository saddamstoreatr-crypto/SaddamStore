package com.sdstore.core.utils

object UrlUtils {
    // --- NAYI TABDEELI: Aapka real bucket URL yahan shamil kar diya gaya hai ---
    private const val CDN_BASE_URL = "https://storage.googleapis.com/saddam-store-atr.firebasestorage.app/"

    fun getCdnUrl(imagePath: String): String {
        // Agar imagePath pehle se hi poora URL hai, to usay wapas bhej dein
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath
        }
        // Agar imagePath aage slash ke sath shuru ho raha hai to usay hata dein
        val correctedPath = if (imagePath.startsWith("/")) imagePath.substring(1) else imagePath
        return "$CDN_BASE_URL$correctedPath"
    }
}