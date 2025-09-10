package com.sdstore.cart.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.R
import com.sdstore.cart.databinding.ItemCartBinding
import com.sdstore.core.models.Sku
import com.sdstore.core.utils.UrlUtils
import com.sdstore.orders.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val onQuantityChange: (sku: Sku, newQuantity: Int) -> Unit,
    private val onRemoveItem: (sku: Sku) -> Unit
) : ListAdapter<Sku, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sku: Sku) {
            binding.tvProductName.text = sku.name

            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            val priceString = format.format(sku.pricePaisas / 100.0)
            binding.tvProductPrice.text = priceString

            binding.tvQuantity.text = sku.quantity.toString()

            val cdnUrl = UrlUtils.getCdnUrl(sku.imageUrl)
            Glide.with(itemView.context)
                .load(cdnUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivProductImage)

            binding.btnIncreaseQuantity.setOnClickListener {
                if (sku.quantity < sku.stockQuantity) {
                    onQuantityChange(sku, sku.quantity + 1)
                } else {
                    Toast.makeText(itemView.context, R.string.stock_limit_reached, Toast.LENGTH_SHORT).show()
                }
            }
            binding.btnDecreaseQuantity.setOnClickListener {
                if (sku.quantity > 1) {
                    onQuantityChange(sku, sku.quantity - 1)
                } else {
                    onRemoveItem(sku)
                }
            }
            binding.btnRemoveItem.setOnClickListener {
                onRemoveItem(sku)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<Sku>() {
    override fun areItemsTheSame(oldItem: Sku, newItem: Sku): Boolean {
        return oldItem.uniqueSkuId == newItem.uniqueSkuId
    }

    override fun areContentsTheSame(oldItem: Sku, newItem: Sku): Boolean {
        return oldItem == newItem
    }
}