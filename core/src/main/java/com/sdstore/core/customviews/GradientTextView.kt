package com.sdstore.core.customviews

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat
import com.sdstore.core.R

class GradientTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CustomTextView(context, attrs) {

    private val startColor: Int by lazy {
        try {
            ContextCompat.getColor(context, R.color.starship)
        } catch (e: Exception) {
            Log.e("GradientTextView", "R.color.starship not found, using default text color.")
            currentTextColor
        }
    }
    private val endColor: Int by lazy {
        try {
            ContextCompat.getColor(context, R.color.geebung)
        } catch (e: Exception) {
            Log.e("GradientTextView", "R.color.geebung not found, using default text color.")
            currentTextColor
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                startColor, endColor, Shader.TileMode.CLAMP
            )
        }
    }
}