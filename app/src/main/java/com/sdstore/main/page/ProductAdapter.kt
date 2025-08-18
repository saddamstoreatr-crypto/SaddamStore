package com.sdstore.main.page

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.R
import com.sdstore.databinding.ItemProductBinding
import com.sdstore.models.Sku
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var products: List<Sku>,
    private val onAddToCartClick: (Sku) -> Unit,
    private val onNotifyMeClick: (Sku) -> Unit,
    private val onItemClick: (Sku) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.apply {
            tvProductName.text = product.name
            tvUnitPrice.text = product.unitPrice

            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            format.maximumFractionDigits = 2
            tvTotalPrice.text = format.format(product.pricePaisas / 100.0)

            // Glide میں پلیس ہولڈر اور ایرر آئیکنز شامل کریں
            Glide.with(holder.itemView.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_placeholder) // لوڈنگ کے دوران دکھانے کے لیے
                .error(R.drawable.ic_broken_image)   // ایرر کی صورت میں دکھانے کے لیے
                .into(ivProductImage)

            // اسٹاک کی حالت کو سنبھالیں
            if (product.inStock) {
                btnAddToCart.visibility = View.VISIBLE
                btnNotifyMe.visibility = View.GONE
                btnAddToCart.setOnClickListener { onAddToCartClick(product) }
            } else {
                btnAddToCart.visibility = View.GONE
                btnNotifyMe.visibility = View.VISIBLE
                btnNotifyMe.setOnClickListener { onNotifyMeClick(product) }
            }

            // اسٹیکرز شامل کریں
            stickerContainer.removeAllViews() // پرانے اسٹیکرز صاف کریں
            product.stickers.forEach { stickerText ->
                val stickerView = TextView(holder.itemView.context).apply {
                    text = stickerText
                    // یہاں آپ اسٹیکر کو مزید اسٹائل دے سکتے ہیں (مثلاً، پس منظر کا رنگ، ٹیکسٹ کا رنگ)
                    setBackgroundColor(Color.parseColor("#E53935")) // مثال کے طور پر سرخ رنگ
                    setTextColor(Color.WHITE)
                    setPadding(8, 4, 8, 4)
                    textSize = 10f
                }
                stickerContainer.addView(stickerView)
            }

            // پورے کارڈ پر کلک کرنے کے لیے
            holder.itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    fun updateProducts(newProducts: List<Sku>) {
        products = newProducts
        notifyDataSetChanged()
    }
}