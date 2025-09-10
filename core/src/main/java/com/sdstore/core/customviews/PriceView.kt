package com.sdstore.core.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.sdstore.core.databinding.ViewPriceBinding

class PriceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewPriceBinding

    init {
        binding = ViewPriceBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setBasePrice(price: String) {
        val parts = price.split(".")
        binding.baseRupeeTextView.text = parts.getOrNull(0) ?: price
        binding.basePaisasTextView.text = parts.getOrNull(1)?.let { ".$it" } ?: ""
    }

    fun setDiscountedPrice(price: String) {
        val parts = price.split(".")
        binding.discountedRupeeTextView.text = parts.getOrNull(0) ?: price
        binding.discountedPaisasTextView.text = parts.getOrNull(1)?.let { ".$it" } ?: ""
    }
}