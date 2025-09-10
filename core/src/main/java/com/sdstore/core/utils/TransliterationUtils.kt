package com.sdstore.core.utils

object TransliterationUtils {

    private val charMap = mapOf(
        "khh" to "کھ",
        "kh" to "خ", "gh" to "غ", "sh" to "ش", "ch" to "چ", "bh" to "بھ",
        "ph" to "پھ", "th" to "تھ", "dh" to "دھ", "rh" to "رھ",
        "a" to "ا", "b" to "ب", "p" to "پ", "t" to "ت", "j" to "ج",
        "h" to "ہ", "x" to "خ", "d" to "د", "r" to "ر", "z" to "ز",
        "s" to "س", "f" to "ف", "q" to "ق", "k" to "ک", "g" to "گ",
        "l" to "ل", "m" to "م", "n" to "ن", "v" to "و", "w" to "و",
        "y" to "ی", "e" to "ی", "i" to "ی", "o" to "و", "u" to "و",
        " " to " "
    )

    fun romanToUrdu(romanText: String): String {
        val mutableRomanText = romanText.lowercase()
        val urduText = StringBuilder()

        var i = 0
        while (i < mutableRomanText.length) {
            if (i + 1 < mutableRomanText.length) {
                val twoChars = mutableRomanText.substring(i, i + 2)
                if (charMap.containsKey(twoChars)) {
                    urduText.append(charMap[twoChars])
                    i += 2
                    continue
                }
            }
            val oneChar = mutableRomanText.substring(i, i + 1)
            if (charMap.containsKey(oneChar)) {
                urduText.append(charMap[oneChar])
            }
            i += 1
        }
        return urduText.toString()
    }

    fun isUrdu(text: String): Boolean {
        for (char in text) {
            if (Character.UnicodeBlock.of(char) == Character.UnicodeBlock.ARABIC) {
                return true
            }
        }
        return false
    }
}