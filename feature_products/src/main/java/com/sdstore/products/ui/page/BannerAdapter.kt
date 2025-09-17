package com.sdstore.products.ui.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.core.models.Banner
import com.sdstore.feature_products.databinding.ItemBannerImageBinding
import com.sdstore.core.R as CoreR

class BannerAdapter(
    private val onBannerClick: (Banner) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    private var banners: List<Banner> = emptyList()

    fun setBanners(banners: List<Banner>) {
        this.banners = banners
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(banners[position])
    }

    override fun getItemCount(): Int = banners.size

    inner class BannerViewHolder(private val binding: ItemBannerImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onBannerClick(banners[adapterPosition])
            }
        }

        fun bind(banner: Banner) {
            Glide.with(binding.root.context)
                .load(banner.imageUrl)
                .placeholder(CoreR.drawable.ic_placeholder)
                .into(binding.ivBanner)
        }
    }
}