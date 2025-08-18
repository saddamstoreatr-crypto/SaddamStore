package com.sdstore.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

open class CustomTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var originalText: CharSequence? = ""

    override fun setText(text: CharSequence?, type: BufferType?) {
        this.originalText = text
        // Urdu text formatting logic can be added here if needed
        super.setText(text, type)
    }

    override fun getText(): CharSequence? {
        return originalText
    }
}