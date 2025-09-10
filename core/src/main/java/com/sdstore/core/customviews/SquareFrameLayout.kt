package com.sdstore.core.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class SquareFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // width ko height ke barabar set karein
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}