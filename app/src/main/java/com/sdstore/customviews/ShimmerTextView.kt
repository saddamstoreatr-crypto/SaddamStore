package com.sdstore.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.sdstore.databinding.ShimmerTextViewBinding

class ShimmerTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ShimmerTextViewBinding

    init {
        binding = ShimmerTextViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setText(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.shimmerContainer.startShimmer()
            binding.shimmerContainer.visibility = View.VISIBLE
            binding.shimmerTextView.visibility = View.GONE
        } else {
            binding.shimmerContainer.stopShimmer()
            binding.shimmerContainer.visibility = View.GONE
            binding.shimmerTextView.visibility = View.VISIBLE
            binding.shimmerTextView.text = text
        }
    }
}