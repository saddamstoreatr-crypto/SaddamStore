package com.sdstore.customviews

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.sdstore.R

class GradientTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CustomTextView(context, attrs) {

    private val startColor = ContextCompat.getColor(context, R.color.starship)
    private val endColor = ContextCompat.getColor(context, R.color.geebung)

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