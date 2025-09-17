package com.sdstore.cart.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.core.models.CartItem
import com.sdstore.feature_cart.R // Fix: R file ko import kiya gaya hai.
import com.sdstore.feature_cart.databinding.ItemCartBinding

interface CartItemListener {
    fun onIncreaseQuantity(item: CartItem)
    fun onDecreaseQuantity(item: CartItem)
    fun onRemoveItem(item: CartItem)
}

class CartAdapter(private val listener: CartItemListener) :
    ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.apply {
                tvProductName.text = item.sku.name
                // Fix: 'price' ab sku object se sahi tarah access hoga.
                tvProductPrice.text = "Rs ${item.sku.price}"
                tvQuantity.text = item.quantity.toString()

                Glide.with(itemView.context)
                    .load(item.sku.imageUrl)
                    // Fix: 'drawable' ab R file se theek access hoga.
                    .placeholder(R.drawable.ic_placeholder)
                    .into(ivProductImage)

                // Fix: Tamam buttons ab binding object se theek access honge.
                binding.btnIncreaseQuantity.setOnClickListener { listener.onIncreaseQuantity(item) }
                binding.btnDecreaseQuantity.setOnClickListener { listener.onDecreaseQuantity(item) }
                binding.btnRemoveItem.setOnClickListener { listener.onRemoveItem(item) }
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

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        // Fix: 'id' ab sku object se sahi tarah access hoga.
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.sku.id == newItem.sku.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}