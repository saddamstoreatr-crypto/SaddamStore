package com.sdstore.products.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.core.models.Sku
import com.sdstore.feature_products.R
import com.sdstore.feature_products.databinding.DialogItemDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemDetailDialog : DialogFragment() {

    private var _binding: DialogItemDetailBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by viewModels()
    private var sku: Sku? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sku = it.getParcelable("sku")
        }
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sku?.let {
            Glide.with(this)
                .load(it.imageUrl)
                .into(binding.ivProductImage)
            binding.tvProductName.text = it.name
            binding.tvProductDescription.text = it.description
            binding.tvProductPrice.text = "Rs. ${it.price}"
            binding.tvUnitInfo.text = it.unitInfo
            binding.btnAddToCart.setOnClickListener { _ ->
                cartViewModel.addToCart(it)
                // You can show a toast or something here
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(sku: Sku) =
            ItemDetailDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("sku", sku)
                }
            }
    }
}