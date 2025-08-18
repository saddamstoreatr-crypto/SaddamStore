package com.sdstore.main.itemdetail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdstore.databinding.DialogItemDetailBinding
import com.sdstore.models.Sku
import java.text.NumberFormat
import java.util.Locale

// Note: This is an updated version of the file from response #16
class ItemDetailDialog : BottomSheetDialogFragment() {
    private var _binding: DialogItemDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setOnClickListener { dismiss() }

        val sku = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("selected_sku", Sku::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("selected_sku")
        }

        sku?.let {
            updateUi(it)
        }
    }

    private fun updateUi(sku: Sku) {
        binding.skuNameTextView.text = sku.name
        binding.skuImageView.loadImage(sku.imageUrl)
        // TODO: Add an "Add to Cart" button to your dialog_item_detail.xml layout
        // and then handle the click here. For example:
        /*
        binding.addToCartButton.setOnClickListener {
            // Here you would call a ViewModel to add the item to the cart.
            Toast.makeText(context, "${sku.name} added to cart", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
