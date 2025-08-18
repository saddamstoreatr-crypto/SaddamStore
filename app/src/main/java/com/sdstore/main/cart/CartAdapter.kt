package com.sdstore.main.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.databinding.ItemCartBinding
import com.sdstore.models.Sku
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private var cartItems: List<Sku>,
    private val onRemoveClick: (Sku) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.binding.apply {
            tvProductName.text = item.name
            val priceInRupees = item.pricePaisas / 100.0
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            tvProductPrice.text = format.format(priceInRupees)
            Glide.with(holder.itemView.context).load(item.imageUrl).into(ivProductImage)

            btnRemoveItem.setOnClickListener {
                onRemoveClick(item)
            }
        }
    }

    fun updateItems(newItems: List<Sku>) {
        cartItems = newItems
        notifyDataSetChanged()
    }
}