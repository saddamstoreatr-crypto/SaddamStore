package com.sdstore.products.ui.banner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.sdstore.core.R
import com.sdstore.feature_products.databinding.FullScreenBannerDialogBinding

class FullScreenBannerDialog : DialogFragment() {

    private var _binding: FullScreenBannerDialogBinding? = null
    private val binding get() = _binding!!
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString("imageUrl")
        }
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FullScreenBannerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.ivBanner)

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(imageUrl: String) =
            FullScreenBannerDialog().apply {
                arguments = Bundle().apply {
                    putString("imageUrl", imageUrl)
                }
            }
    }
}