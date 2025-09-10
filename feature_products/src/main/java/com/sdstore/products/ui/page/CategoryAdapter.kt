package com.sdstore.products.ui.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.R
import com.sdstore.core.models.Category
import com.sdstore.core.utils.UrlUtils
import com.sdstore.products.databinding.ItemHomeCategoryBinding

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ItemHomeCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.tvCategoryName.text = category.name

            if (category.imageUrl == Category.PURCHASED_ITEMS_IMAGE_URL) {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_history)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.ivCategoryImage)
            } else {
                val cdnUrl = UrlUtils.getCdnUrl(category.imageUrl)
                Glide.with(itemView.context)
                    .load(cdnUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .into(binding.ivCategoryImage)
            }

            itemView.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemHomeCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }
}