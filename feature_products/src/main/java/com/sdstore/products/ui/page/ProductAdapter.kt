package com.sdstore.products.ui.page

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.sdstore.R
import com.sdstore.core.models.Sku
import com.sdstore.core.utils.UrlUtils
import com.sdstore.products.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.*

class ProductAdapter(
    private val onCartAction: (sku: Sku, newQuantity: Int) -> Unit,
    private val onItemClick: (Sku) -> Unit
) : ListAdapter<Sku, ProductAdapter.ProductViewHolder>(ProductDiffCallback()),
    ListPreloader.PreloadModelProvider<Sku> {

    private val cartItemsMap = mutableMapOf<String, Int>()
    val preloadSizeProvider = ViewPreloadSizeProvider<Sku>()

    private lateinit var context: Context

    fun updateCartItems(cartItems: List<Sku>) {
        cartItemsMap.clear()
        cartItems.forEach { cartItemsMap[it.uniqueSkuId] = it.quantity }
        notifyItemRangeChanged(0, itemCount, "UPDATE_CART_STATE")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        preloadSizeProvider.setView(binding.ivProductImage)
        context = parent.context
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains("UPDATE_CART_STATE")) {
            holder.updateCartControls(getItem(position), cartItemsMap)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getPreloadItems(position: Int): MutableList<Sku> {
        return if (position < itemCount && getItem(position).imageUrl.isNotBlank()) {
            Collections.singletonList(getItem(position))
        } else {
            Collections.emptyList()
        }
    }

    override fun getPreloadRequestBuilder(item: Sku): RequestBuilder<*> {
        val cdnUrl = UrlUtils.getCdnUrl(item.imageUrl)
        return Glide.with(context)
            .load(cdnUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_broken_image)
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sku: Sku) {
            binding.tvProductName.text = sku.name
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            binding.tvProductPrice.text = format.format(sku.pricePaisas / 100.0)
            binding.tvUnitInfo.text = sku.unitInfo
            binding.tvUnitInfo.visibility = if (sku.unitInfo.isNotEmpty()) View.VISIBLE else View.GONE

            if (sku.imageUrl.isNotBlank()) {
                val cdnUrl = UrlUtils.getCdnUrl(sku.imageUrl)
                Glide.with(itemView.context)
                    .load(cdnUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .override(200, 200)
                    .centerCrop()
                    .thumbnail(0.25f)
                    .into(binding.ivProductImage)
            } else {
                binding.ivProductImage.setImageResource(R.drawable.ic_placeholder)
            }

            val isOutOfStock = sku.stockQuantity <= 0

            if (isOutOfStock) {
                binding.btnAddToCart.visibility = View.GONE
                binding.quantityStepper.visibility = View.GONE
                binding.tvOutOfStock.visibility = View.VISIBLE
                binding.ivProductImage.alpha = 0.5f
                binding.tvProductName.paintFlags = binding.tvProductName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.tvOutOfStock.visibility = View.GONE
                binding.ivProductImage.alpha = 1.0f
                binding.tvProductName.paintFlags = binding.tvProductName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            updateCartControls(sku, cartItemsMap)

            itemView.setOnClickListener { onItemClick(sku) }

            binding.btnAddToCart.setOnClickListener {
                if (!isOutOfStock) {
                    onCartAction(sku, 1)
                }
            }
            binding.btnIncreaseQuantity.setOnClickListener {
                val currentQuantity = cartItemsMap[sku.uniqueSkuId] ?: 0
                if (currentQuantity < sku.stockQuantity) {
                    onCartAction(sku, currentQuantity + 1)
                } else {
                    Toast.makeText(itemView.context, R.string.stock_limit_reached, Toast.LENGTH_SHORT).show()
                }
            }
            binding.btnDecreaseQuantity.setOnClickListener {
                val currentQuantity = cartItemsMap[sku.uniqueSkuId] ?: 0
                onCartAction(sku, currentQuantity - 1)
            }
        }

        fun updateCartControls(sku: Sku, cartItemsMap: Map<String, Int>) {
            val isOutOfStock = sku.stockQuantity <= 0
            if (!isOutOfStock) {
                val quantityInCart = cartItemsMap[sku.uniqueSkuId] ?: 0
                if (quantityInCart > 0) {
                    binding.btnAddToCart.visibility = View.GONE
                    binding.quantityStepper.visibility = View.VISIBLE
                    binding.tvQuantity.text = quantityInCart.toString()
                } else {
                    binding.btnAddToCart.visibility = View.VISIBLE
                    binding.quantityStepper.visibility = View.GONE
                }
            }
        }
    }
}

class ProductDiffCallback : DiffUtil.ItemCallback<Sku>() {
    override fun areItemsTheSame(oldItem: Sku, newItem: Sku): Boolean {
        return oldItem.uniqueSkuId == newItem.uniqueSkuId
    }

    override fun areContentsTheSame(oldItem: Sku, newItem: Sku): Boolean {
        return oldItem == newItem
    }
}