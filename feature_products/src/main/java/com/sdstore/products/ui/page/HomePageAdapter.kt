package com.sdstore.products.ui.page

import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.sdstore.R
import com.sdstore.core.models.Banner
import com.sdstore.core.models.Category
import com.sdstore.core.models.OrderItem
import com.sdstore.core.models.Sku
import com.sdstore.products.databinding.*
import com.sdstore.products.ui.banner.FullScreenBannerDialog
import java.text.NumberFormat
import java.util.*

internal const val ITEM_TYPE_BANNERS = 0
internal const val ITEM_TYPE_CATEGORIES = 1
internal const val ITEM_TYPE_TITLE = 2
internal const val ITEM_TYPE_PRODUCT = 3
internal const val ITEM_TYPE_REGULAR_ITEMS = 4
internal const val ITEM_TYPE_LOADING = 5
internal const val ITEM_TYPE_RECENT_ITEMS = 6

class HomePageAdapter(
    private val onCategoryClick: (Category) -> Unit,
    private val onProductClick: (Sku) -> Unit,
    private val onCartAction: (Sku, Int) -> Unit
) : ListAdapter<HomePageItem, RecyclerView.ViewHolder>(HomePageDiffCallback()) {

    private val cartItemsMap = mutableMapOf<String, Int>()

    fun updateCartItems(cartItems: List<Sku>) {
        cartItemsMap.clear()
        cartItems.forEach { cartItemsMap[it.uniqueSkuId] = it.quantity }
        notifyItemRangeChanged(0, itemCount, "UPDATE_CART_STATE")
    }

    class LoadingViewHolder(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    inner class RecentItemsViewHolder(private val binding: ViewholderRegularItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: List<OrderItem>) {
            binding.rvRegularItems.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

            val skus = items.map { item ->
                Sku(
                    uniqueSkuId = item.uniqueSkuId,
                    name = item.name,
                    imageUrl = item.imageUrl,
                    pricePaisas = item.pricePaisas,
                    stockQuantity = 100
                )
            }

            val adapter = HorizontalItemsAdapter()
            binding.rvRegularItems.adapter = adapter
            adapter.submitList(skus)
        }
    }

    inner class BannerViewHolder(private val binding: ViewholderBannersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var handler = Handler(Looper.getMainLooper())
        private lateinit var runnable: Runnable

        fun bind(banners: List<Banner>) {
            val bannerAdapter = BannerAdapter { banner ->
                if (!banner.imageUrl.isNullOrEmpty()) {
                    val fragmentManager = (binding.root.context as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager
                    fragmentManager?.let {
                        FullScreenBannerDialog.newInstance(banner.imageUrl).show(it, FullScreenBannerDialog.TAG)
                    }
                }
            }
            binding.viewPagerBanners.adapter = bannerAdapter
            bannerAdapter.submitList(banners)
            TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerBanners) { _, _ -> }.attach()

            if (banners.size > 1) {
                runnable = Runnable {
                    var currentItem = binding.viewPagerBanners.currentItem
                    currentItem++
                    if (currentItem >= bannerAdapter.itemCount) {
                        currentItem = 0
                    }
                    binding.viewPagerBanners.setCurrentItem(currentItem, true)
                }
                binding.viewPagerBanners.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        handler.removeCallbacks(runnable)
                        handler.postDelayed(runnable, 3000)
                    }
                })
                handler.postDelayed(runnable, 3000)
            }
        }

        fun onDetached() {
            if (::runnable.isInitialized) {
                handler.removeCallbacks(runnable)
            }
        }
    }

    inner class CategoriesViewHolder(private val binding: ViewholderCategoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categories: List<Category>) {
            if (categories.isEmpty()) {
                itemView.visibility = View.GONE
                return
            } else {
                itemView.visibility = View.VISIBLE
            }

            val categoryAdapter = CategoryAdapter(categories, onCategoryClick)
            binding.rvCategories.apply {
                layoutManager = GridLayoutManager(itemView.context, 2)
                adapter = categoryAdapter
                isNestedScrollingEnabled = false
            }
        }
    }

    inner class TitleViewHolder(private val binding: ItemTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.tvTitle.text = title
        }
    }

    inner class RegularItemsViewHolder(val binding: ViewholderRegularItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(skus: List<Sku>) {
            binding.rvRegularItems.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = HorizontalItemsAdapter()
            binding.rvRegularItems.adapter = adapter
            adapter.submitList(skus)
        }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sku: Sku) {
            binding.tvProductName.text = sku.name
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            binding.tvProductPrice.text = format.format(sku.pricePaisas / 100.0)
            binding.tvUnitInfo.text = sku.unitInfo
            binding.tvUnitInfo.visibility = if (sku.unitInfo.isNotEmpty()) View.VISIBLE else View.GONE

            if (sku.imageUrl.isNotBlank()) {
                Glide.with(itemView.context)
                    .load(sku.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .thumbnail(0.25f)
                    .into(binding.ivProductImage)
            } else {
                binding.ivProductImage.setImageResource(R.drawable.ic_placeholder)
            }

            updateCartControls(sku)

            itemView.setOnClickListener { onProductClick(sku) }

            binding.btnAddToCart.setOnClickListener { onCartAction(sku, 1) }
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

        fun updateCartControls(sku: Sku) {
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

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomePageItem.Banners -> ITEM_TYPE_BANNERS
            is HomePageItem.CategoriesList -> ITEM_TYPE_CATEGORIES
            is HomePageItem.Title -> ITEM_TYPE_TITLE
            is HomePageItem.ProductItem -> ITEM_TYPE_PRODUCT
            is HomePageItem.RegularItems -> ITEM_TYPE_REGULAR_ITEMS
            is HomePageItem.Loading -> ITEM_TYPE_LOADING
            is HomePageItem.RecentItems -> ITEM_TYPE_RECENT_ITEMS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_BANNERS -> BannerViewHolder(ViewholderBannersBinding.inflate(inflater, parent, false))
            ITEM_TYPE_CATEGORIES -> CategoriesViewHolder(ViewholderCategoryListBinding.inflate(inflater, parent, false))
            ITEM_TYPE_TITLE -> TitleViewHolder(ItemTitleBinding.inflate(inflater, parent, false))
            ITEM_TYPE_PRODUCT -> ProductViewHolder(ItemProductBinding.inflate(inflater, parent, false))
            ITEM_TYPE_REGULAR_ITEMS -> RegularItemsViewHolder(ViewholderRegularItemsBinding.inflate(inflater, parent, false))
            ITEM_TYPE_LOADING -> LoadingViewHolder(ItemLoadingBinding.inflate(inflater, parent, false))
            ITEM_TYPE_RECENT_ITEMS -> RecentItemsViewHolder(ViewholderRegularItemsBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomePageItem.Banners -> (holder as BannerViewHolder).bind(item.banners)
            is HomePageItem.CategoriesList -> (holder as CategoriesViewHolder).bind(item.categories)
            is HomePageItem.Title -> (holder as TitleViewHolder).bind(item.text)
            is HomePageItem.ProductItem -> (holder as ProductViewHolder).bind(item.sku)
            is HomePageItem.RegularItems -> (holder as RegularItemsViewHolder).bind(item.skus)
            is HomePageItem.Loading -> { }
            is HomePageItem.RecentItems -> (holder as RecentItemsViewHolder).bind(item.items)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains("UPDATE_CART_STATE")) {
            when (val item = getItem(position)) {
                is HomePageItem.ProductItem -> (holder as ProductViewHolder).updateCartControls(item.sku)
                is HomePageItem.RegularItems -> (holder as RegularItemsViewHolder).bind(item.skus)
                is HomePageItem.RecentItems -> (holder as RecentItemsViewHolder).bind(item.items)
                else -> super.onBindViewHolder(holder, position, payloads)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is BannerViewHolder) {
            holder.onDetached()
        }
    }

    private inner class HorizontalItemsAdapter :
        ListAdapter<Sku, HorizontalItemsAdapter.ViewHolder>(SkuDiffCallback()) {

        inner class ViewHolder(private val binding: ItemProductHorizontalBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(sku: Sku) {
                binding.tvProductName.text = sku.name
                val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
                binding.tvProductPrice.text = format.format(sku.pricePaisas / 100.0)
                binding.tvUnitInfo.visibility = if (sku.unitInfo.isNotEmpty()) View.VISIBLE else View.GONE
                binding.tvUnitInfo.text = sku.unitInfo

                Glide.with(itemView.context).load(sku.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .thumbnail(0.25f)
                    .into(binding.ivProductImage)

                updateCartControls(sku)

                itemView.setOnClickListener { onProductClick(sku) }
                binding.btnAddToCart.setOnClickListener { onCartAction(sku, 1) }
                binding.btnIncreaseQuantity.setOnClickListener {
                    val currentQuantity = cartItemsMap[sku.uniqueSkuId] ?: 0
                    if (currentQuantity < sku.stockQuantity) onCartAction(sku, currentQuantity + 1)
                    else Toast.makeText(itemView.context, R.string.stock_limit_reached, Toast.LENGTH_SHORT).show()
                }
                binding.btnDecreaseQuantity.setOnClickListener {
                    val currentQuantity = cartItemsMap[sku.uniqueSkuId] ?: 0
                    onCartAction(sku, currentQuantity - 1)
                }
            }

            fun updateCartControls(sku: Sku) {
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemProductHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
            if (payloads.contains("UPDATE_CART_STATE")) {
                holder.updateCartControls(getItem(position))
            } else {
                super.onBindViewHolder(holder, position, payloads)
            }
        }
    }
}

class HomePageDiffCallback : DiffUtil.ItemCallback<HomePageItem>() {
    override fun areItemsTheSame(oldItem: HomePageItem, newItem: HomePageItem): Boolean {
        return when {
            oldItem is HomePageItem.ProductItem && newItem is HomePageItem.ProductItem -> oldItem.sku.uniqueSkuId == newItem.sku.uniqueSkuId
            oldItem is HomePageItem.Title && newItem is HomePageItem.Title -> oldItem.text == newItem.text
            oldItem is HomePageItem.Banners && newItem is HomePageItem.Banners -> true
            oldItem is HomePageItem.CategoriesList && newItem is HomePageItem.CategoriesList -> true
            oldItem is HomePageItem.RegularItems && newItem is HomePageItem.RegularItems -> true
            oldItem is HomePageItem.RecentItems && newItem is HomePageItem.RecentItems -> true
            oldItem is HomePageItem.Loading && newItem is HomePageItem.Loading -> true
            else -> oldItem == newItem
        }
    }

    override fun areContentsTheSame(oldItem: HomePageItem, newItem: HomePageItem): Boolean {
        return oldItem == newItem
    }
}

class SkuDiffCallback : DiffUtil.ItemCallback<Sku>() {
    override fun areItemsTheSame(oldItem: Sku, newItem: Sku): Boolean = oldItem.uniqueSkuId == newItem.uniqueSkuId
    override fun areContentsTheSame(oldItem: Sku, newItem: Sku): Boolean = oldItem == newItem
}