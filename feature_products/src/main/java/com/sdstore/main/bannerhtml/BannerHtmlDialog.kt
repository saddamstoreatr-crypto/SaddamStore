package com.sdstore.main.bannerhtml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.DialogFragment
import com.sdstore.feature_products.R
import com.sdstore.feature_products.databinding.BannerHtmlDialogBinding

class BannerHtmlDialog : DialogFragment() {

    private var _binding: BannerHtmlDialogBinding? = null
    private val binding get() = _binding!!

    private var htmlContent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            htmlContent = it.getString("htmlContent")
        }
        setStyle(STYLE_NO_FRAME, R.style.Theme_SaddamStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BannerHtmlDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        htmlContent?.let {
            binding.webView.loadData(it, "text/html", "UTF-8")
        }
        binding.backButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(htmlContent: String) =
            BannerHtmlDialog().apply {
                arguments = Bundle().apply {
                    putString("htmlContent", htmlContent)
                }
            }
    }
}