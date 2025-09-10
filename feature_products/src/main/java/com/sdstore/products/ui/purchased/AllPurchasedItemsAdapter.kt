package com.sdstore.products.ui.purchased

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.R
import com.sdstore.core.models.OrderItem
import com.sdstore.core.models.Sku
import com.sdstore.core.utils.UrlUtils
import com.sdstore.products.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class AllPurchasedItemsAdapter(
    private val onCartAction: (Sku, Int) -> Unit,
    private val onItemClick: (Sku) -> Unit
) : ListAdapter<OrderItem, AllPurchasedItemsAdapter.ViewHolder>(DiffCallback()) {

    private val cartItemsMap = mutableMapOf<String, Int>()

    fun updateCartItems(cartItems: List<Sku>) {
        cartItemsMap.clear()
        cartItems.forEach { cartItemsMap[it.uniqueSkuId] = it.quantity }
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItem) {
            val sku = Sku(
                uniqueSkuId = item.uniqueSkuId,
                name = item.name,
                imageUrl = item.imageUrl,
                pricePaisas = item.pricePaisas,
                stockQuantity = 100 // Assume previously purchased items are in stock
            )

            binding.tvProductName.text = item.name
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            binding.tvProductPrice.text = format.format(item.pricePaisas / 100.0)
            binding.tvUnitInfo.visibility = View.GONE

            val cdnUrl = UrlUtils.getCdnUrl(item.imageUrl)
            Glide.with(itemView.context).load(cdnUrl).placeholder(R.drawable.ic_placeholder).into(binding.ivProductImage)

            val quantityInCart = cartItemsMap[item.uniqueSkuId] ?: 0
            if (quantityInCart > 0) {
                binding.btnAddToCart.visibility = View.GONE
                binding.quantityStepper.visibility = View.VISIBLE
                binding.tvQuantity.text = quantityInCart.toString()
            } else {
                binding.btnAddToCart.visibility = View.VISIBLE
                binding.quantityStepper.visibility = View.GONE
            }

            binding.tvOutOfStock.visibility = View.GONE
            binding.ivProductImage.alpha = 1.0f
            binding.tvProductName.paintFlags = binding.tvProductName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            itemView.setOnClickListener { onItemClick(sku) }
            binding.btnAddToCart.setOnClickListener { onCartAction(sku, 1) }
            binding.btnIncreaseQuantity.setOnClickListener { onCartAction(sku, quantityInCart + 1) }
            binding.btnDecreaseQuantity.setOnClickListener { onCartAction(sku, quantityInCart - 1) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean = oldItem.uniqueSkuId == newItem.uniqueSkuId
        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean = oldItem == newItem
    }
}