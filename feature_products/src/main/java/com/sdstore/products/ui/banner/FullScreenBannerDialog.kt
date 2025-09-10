package com.sdstore.products.ui.banner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.sdstore.R
import com.sdstore.products.databinding.FullScreenBannerDialogBinding

class FullScreenBannerDialog : DialogFragment() {

    private var _binding: FullScreenBannerDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FullScreenBannerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUrl = arguments?.getString(ARG_IMAGE_URL)

        imageUrl?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.fullScreenBannerImageView)
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FullScreenBannerDialog"
        private const val ARG_IMAGE_URL = "image_url"

        fun newInstance(imageUrl: String): FullScreenBannerDialog {
            val fragment = FullScreenBannerDialog()
            val args = Bundle().apply {
                putString(ARG_IMAGE_URL, imageUrl)
            }
            fragment.arguments = args
            return fragment
        }
    }
}