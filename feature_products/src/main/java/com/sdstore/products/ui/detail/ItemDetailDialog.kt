package com.sdstore.products.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdstore.R
import com.sdstore.cart.viewmodels.CartViewModel
import com.sdstore.core.models.Sku
import com.sdstore.products.databinding.DialogItemDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemDetailDialog : BottomSheetDialogFragment() {
    private var _binding: DialogItemDetailBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setOnClickListener { dismiss() }

        val sku = arguments?.getParcelable<Sku>("selected_sku")

        sku?.let {
            updateUi(it)
        }
    }

    private fun updateUi(sku: Sku) {
        binding.skuNameTextView.text = sku.name

        Glide.with(this)
            .load(sku.imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.skuImageView)

        val isOutOfStock = sku.stockQuantity <= 0
        if (isOutOfStock) {
            binding.addToCartButton.isEnabled = false
            binding.addToCartButton.text = getString(R.string.out_of_stock)
            binding.addToCartButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.coolGrey5))
        } else {
            binding.addToCartButton.isEnabled = true
            binding.addToCartButton.text = getString(R.string.add_to_cart)
            binding.addToCartButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.brand_green))

            binding.addToCartButton.setOnClickListener {
                cartViewModel.updateItemQuantity(sku, 1)
                Toast.makeText(context, getString(R.string.item_added_to_cart, sku.name), Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(sku: Sku): ItemDetailDialog {
            val dialog = ItemDetailDialog()
            val args = Bundle().apply {
                putParcelable("selected_sku", sku)
            }
            dialog.arguments = args
            return dialog
        }
    }
}