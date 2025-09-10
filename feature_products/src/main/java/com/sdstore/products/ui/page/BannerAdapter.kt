package com.sdstore.products.ui.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.R
import com.sdstore.core.models.Banner
import com.sdstore.core.utils.UrlUtils
import com.sdstore.products.databinding.ItemBannerImageBinding
import com.sdstore.products.ui.banner.FullScreenBannerDialog

class BannerAdapter(
    private val onBannerClick: (Banner) -> Unit
) : ListAdapter<Banner, BannerAdapter.BannerImageViewHolder>(BannerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerImageViewHolder {
        val binding = ItemBannerImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BannerImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BannerImageViewHolder(private val binding: ItemBannerImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(banner: Banner) {
            val cdnUrl = UrlUtils.getCdnUrl(banner.imageUrl)
            Glide.with(itemView.context)
                .load(cdnUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivBannerImage)

            itemView.setOnClickListener {
                if (!banner.imageUrl.isNullOrEmpty()) {
                    val fragmentManager = (itemView.context as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager
                    fragmentManager?.let {
                        FullScreenBannerDialog.newInstance(cdnUrl).show(it, FullScreenBannerDialog.TAG)
                    }
                }
                onBannerClick(banner)
            }
        }
    }

    private class BannerDiffCallback : DiffUtil.ItemCallback<Banner>() {
        override fun areItemsTheSame(oldItem: Banner, newItem: Banner): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }

        override fun areContentsTheSame(oldItem: Banner, newItem: Banner): Boolean {
            return oldItem == newItem
        }
    }
}