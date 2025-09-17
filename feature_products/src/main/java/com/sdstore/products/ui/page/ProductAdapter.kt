package com.sdstore.products.ui.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.core.models.Sku
import com.sdstore.feature_products.R
import com.sdstore.feature_products.databinding.ItemProductBinding

class ProductAdapter(
    private val onItemClick: (Sku) -> Unit,
    private val onAddToCartClick: (Sku) -> Unit,
    private val onIncreaseClick: (Sku) -> Unit,
    private val onDecreaseClick: (Sku) -> Unit
) : ListAdapter<Sku, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
            binding.btnAddToCart.setOnClickListener {
                onAddToCartClick(getItem(adapterPosition))
            }
            binding.btnIncrease.setOnClickListener {
                onIncreaseClick(getItem(adapterPosition))
            }
            binding.btnDecrease.setOnClickListener {
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

            // Quantity controls visibility logic here
        }
    }
}

class ProductDiffCallback : DiffUtil.ItemCallback<Sku>() {
    override fun areItemsTheSame(oldItem: Sku, newItem: Sku): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Sku, newItem: Sku): Boolean {
        return oldItem == newItem
    }
}