package com.sdstore.customviews

import android.content.Context
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatEditText
import com.sdstore.R

class AutoSizeEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var minTextSize: Float = 0f
    private var maxTextSize: Float = 0f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AutoSizeEditText)
        minTextSize = a.getDimension(R.styleable.AutoSizeEditText_minTextSize, 10f)
        maxTextSize = a.getDimension(R.styleable.AutoSizeEditText_maxTextSize, textSize)
        a.recycle()
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        adjustTextSize()
    }

    private fun adjustTextSize() {
        val text = text.toString()
        if (text.isEmpty() || width <= 0) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, maxTextSize)
            return
        }

        val availableWidth = (width - paddingLeft - paddingRight).toFloat()
        var low = minTextSize
        var high = maxTextSize
        var targetSize = low

        val paint = TextPaint(paint)

        while (low <= high) {
            val mid = (low + high) / 2
            paint.textSize = mid
            if (paint.measureText(text) <= availableWidth) {
                targetSize = mid
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw) {
            adjustTextSize()
        }
    }
}