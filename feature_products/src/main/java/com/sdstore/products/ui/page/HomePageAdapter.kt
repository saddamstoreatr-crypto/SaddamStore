package com.sdstore.products.ui.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.core.models.Banner
import com.sdstore.core.models.Category
import com.sdstore.core.models.Sku
import com.sdstore.feature_products.databinding.*

class HomePageAdapter(
    private val onBannerClick: (Banner) -> Unit,
    private val onCategoryClick: (Category) -> Unit,
    private val onProductClick: (Sku) -> Unit,
    private val onAddToCartClick: (Sku) -> Unit,
    private val onIncreaseClick: (Sku) -> Unit,
    private val onDecreaseClick: (Sku) -> Unit,
    private val onViewAllClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<HomePageItem>()

    fun submitList(newItems: List<HomePageItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HomePageItem.Banners -> VIEW_TYPE_BANNERS
            is HomePageItem.CategoriesList -> VIEW_TYPE_CATEGORIES
            is HomePageItem.ProductItem -> VIEW_TYPE_PRODUCT_ITEM
            is HomePageItem.Title -> VIEW_TYPE_TITLE
            is HomePageItem.RegularItems -> VIEW_TYPE_REGULAR_ITEMS
            is HomePageItem.RecentItems -> VIEW_TYPE_RECENT_ITEMS
            is HomePageItem.Loading -> VIEW_TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_BANNERS -> BannersViewHolder(
                ViewholderBannersBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_CATEGORIES -> CategoriesViewHolder(
                ViewholderCategoryListBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_REGULAR_ITEMS -> RegularItemsViewHolder(
                ViewholderRegularItemsBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_TITLE -> TitleViewHolder(
                ItemTitleBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_PRODUCT_ITEM -> ProductItemViewHolder(
                ItemProductBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_RECENT_ITEMS -> RecentItemsViewHolder(
                ItemRecentOrderBinding.inflate(inflater, parent, false)
            )
            else -> LoadingViewHolder(
                ItemLoadingBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HomePageItem.Banners -> (holder as BannersViewHolder).bind(item.banners)
            is HomePageItem.CategoriesList -> (holder as CategoriesViewHolder).bind(item.categories)
            is HomePageItem.ProductItem -> (holder as ProductItemViewHolder).bind(item.sku)
            is HomePageItem.RegularItems -> (holder as RegularItemsViewHolder).bind(item)
            is HomePageItem.Title -> (holder as TitleViewHolder).bind(item)
            is HomePageItem.RecentItems -> (holder as RecentItemsViewHolder).bind(item.items)
            is HomePageItem.Loading -> { /* No data to bind for loading state */ }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class BannersViewHolder(private val binding: ViewholderBannersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val bannerAdapter = BannerAdapter(onBannerClick)
        init {
            binding.bannerViewPager.adapter = bannerAdapter
        }

        fun bind(banners: List<Banner>) {
            bannerAdapter.setBanners(banners)
        }
    }

    inner class CategoriesViewHolder(private val binding: ViewholderCategoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val categoryAdapter = CategoryAdapter(onCategoryClick)
        init {
            binding.rvCategories.layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            binding.rvCategories.adapter = categoryAdapter
        }
        fun bind(categories: List<Category>) {
            categoryAdapter.setCategories(categories)
        }
    }

    inner class RegularItemsViewHolder(private val binding: ViewholderRegularItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val productAdapter = ProductAdapter(
            onProductClick,
            onAddToCartClick,
            onIncreaseClick,
            onDecreaseClick
        )
        init {
            binding.rvRegularItems.layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            binding.rvRegularItems.adapter = productAdapter
        }

        fun bind(item: HomePageItem.RegularItems) {
            productAdapter.submitList(item.skus)
            binding.tvTitle.text = item.title
            binding.tvViewAll.setOnClickListener { onViewAllClick(item.title) }
        }
    }

    inner class TitleViewHolder(private val binding: ItemTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomePageItem.Title) {
            binding.tvTitle.text = item.text
            binding.tvViewAll.setOnClickListener { onViewAllClick(item.text) }
        }
    }

    inner class ProductItemViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sku: Sku) {
            // Bind SKU data to the views in item_product.xml
        }
    }

    inner class RecentItemsViewHolder(private val binding: ItemRecentOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: List<com.sdstore.core.models.OrderItem>) {
            // Bind recent items data to the views in item_recent_order.xml
        }
    }

    class LoadingViewHolder(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_BANNERS = 0
        private const val VIEW_TYPE_CATEGORIES = 1
        private const val VIEW_TYPE_REGULAR_ITEMS = 2
        private const val VIEW_TYPE_TITLE = 3
        private const val VIEW_TYPE_LOADING = 4
        private const val VIEW_TYPE_PRODUCT_ITEM = 5
        private const val VIEW_TYPE_RECENT_ITEMS = 6
    }
}