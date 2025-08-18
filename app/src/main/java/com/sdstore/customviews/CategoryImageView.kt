package com.sdstore.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sdstore.databinding.CategoryImageViewBinding

class CategoryImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: CategoryImageViewBinding

    init {
        binding = CategoryImageViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    // Yahan image aur text set karne ka logic aayega
}