package com.sdstore.products.ui.purchased

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.core.R
import com.sdstore.core.models.Sku
import com.sdstore.feature_products.databinding.ItemProductHorizontalBinding

class AllPurchasedItemsAdapter(
    private val onAddToCartClick: (Sku) -> Unit,
    private val onIncreaseClick: (Sku) -> Unit,
    private val onDecreaseClick: (Sku) -> Unit
) : ListAdapter<Sku, AllPurchasedItemsAdapter.PurchasedItemViewHolder>(PurchasedItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasedItemViewHolder {
        val binding = ItemProductHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PurchasedItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PurchasedItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PurchasedItemViewHolder(private val binding: ItemProductHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnAddToCart.setOnClickListener {
                onAddToCartClick(getItem(adapterPosition))
            }
            binding.btnIncreaseQuantity.setOnClickListener {
                onIncreaseClick(getItem(adapterPosition))
            }
            binding.btnDecreaseQuantity.setOnClickListener {
                onDecreaseClick(getItem(adapterPosition))
            }
        }

        fun bind(sku: Sku) {
            binding.tvProductName.text = sku.name
            binding.tvProductPrice.text = "Rs. ${sku.price}"
            Glide.with(binding.root.context)
                .load(sku.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivProductImage)
        }
    }
}

class PurchasedItemDiffCallback : DiffUtil.ItemCallback<Sku>() {
    override fun areItemsTheSame(oldItem: Sku, newItem: Sku): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Sku, newItem: Sku): Boolean {
        return oldItem == newItem
    }
}