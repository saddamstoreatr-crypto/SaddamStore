package com.sdstore.core.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.sdstore.core.R
import com.sdstore.core.databinding.CategoryImageViewBinding
import com.sdstore.core.utils.UrlUtils

class CategoryImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: CategoryImageViewBinding

    init {
        binding = CategoryImageViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    // --- NAYI TABDEELI: Image aur text set karne ka logic shamil kiya gaya hai ---
    fun setImage(imageUrl: String?) {
        val cdnUrl = imageUrl?.let { UrlUtils.getCdnUrl(it) }
        Glide.with(context)
            .load(cdnUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_broken_image)
            .into(binding.ivCategoryImage)
    }

    fun setText(name: String?) {
        binding.tvCategoryName.text = name
    }
}