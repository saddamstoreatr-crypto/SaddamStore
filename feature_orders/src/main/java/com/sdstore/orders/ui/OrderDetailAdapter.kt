package com.sdstore.orders.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sdstore.R
import com.sdstore.databinding.ItemCartBinding
import com.sdstore.databinding.ItemOrderDetailFooterBinding
import com.sdstore.databinding.ItemOrderDetailHeaderBinding
import com.sdstore.core.models.Order
import com.sdstore.core.models.OrderItem
import com.sdstore.orders.databinding.ItemCartBinding
import com.sdstore.orders.databinding.ItemOrderDetailFooterBinding
import com.sdstore.orders.databinding.ItemOrderDetailHeaderBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

sealed class OrderDetailItem {
    data class Header(val order: Order) : OrderDetailItem()
    data class Item(val orderItem: OrderItem) : OrderDetailItem()
    data class Footer(val order: Order) : OrderDetailItem()
}

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ITEM = 1
private const val VIEW_TYPE_FOOTER = 2

class OrderDetailAdapter(
    private val onCancelClick: (String) -> Unit,
    private val onDownloadInvoiceClick: (Order) -> Unit
) : androidx.recyclerview.widget.ListAdapter<OrderDetailItem, RecyclerView.ViewHolder>(OrderDetailDiffCallback()) {

    // --- HEADER VIEW HOLDER ---
    inner class HeaderViewHolder(private val binding: ItemOrderDetailHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            val context = itemView.context
            binding.tvOrderId.text = context.getString(R.string.order_id_prefix, order.orderId)

            val date = order.createdAt
            val formattedDate = if (date != null) {
                SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(date)
            } else {
                context.getString(R.string.order_date_unavailable)
            }
            binding.tvOrderDate.text = formattedDate
            binding.tvOrderStatus.text = order.status

            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            binding.tvOrderTotal.text = format.format(order.totalPrice / 100.0)

            if (order.status.equals("Cancelled", ignoreCase = true) && !order.cancellationReason.isNullOrEmpty()) {
                binding.cancellationReasonCard.visibility = View.VISIBLE
                binding.tvCancellationReason.text = order.cancellationReason
            } else {
                binding.cancellationReasonCard.visibility = View.GONE
            }
        }
    }

    // --- ITEM VIEW HOLDER ---
    inner class ItemViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItem) {
            binding.tvProductName.text = item.name
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            val priceString = format.format(item.pricePaisas / 100.0)
            binding.tvProductPrice.text = priceString
            binding.tvQuantity.text = itemView.context.getString(R.string.quantity_prefix, item.quantity)

            Glide.with(itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.ivProductImage)

            binding.quantitySelector.visibility = View.GONE
            binding.btnRemoveItem.visibility = View.GONE
        }
    }

    // --- FOOTER VIEW HOLDER ---
    inner class FooterViewHolder(private val binding: ItemOrderDetailFooterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            // --- NAYI TABDEELI: Yahan check lagaya gaya hai ---
            if (order.status.equals("Pending", ignoreCase = true)) {
                binding.btnCancelOrder.visibility = View.VISIBLE
                binding.btnCancelOrder.setOnClickListener {
                    onCancelClick(order.orderId)
                }
            } else {
                binding.btnCancelOrder.visibility = View.GONE
            }

            binding.btnDownloadInvoice.setOnClickListener {
                onDownloadInvoiceClick(order)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is OrderDetailItem.Header -> VIEW_TYPE_HEADER
            is OrderDetailItem.Item -> VIEW_TYPE_ITEM
            is OrderDetailItem.Footer -> VIEW_TYPE_FOOTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(ItemOrderDetailHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_ITEM -> ItemViewHolder(ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_FOOTER -> FooterViewHolder(ItemOrderDetailFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is OrderDetailItem.Header -> (holder as HeaderViewHolder).bind(item.order)
            is OrderDetailItem.Item -> (holder as ItemViewHolder).bind(item.orderItem)
            is OrderDetailItem.Footer -> (holder as FooterViewHolder).bind(item.order)
        }
    }
}

class OrderDetailDiffCallback : DiffUtil.ItemCallback<OrderDetailItem>() {
    override fun areItemsTheSame(oldItem: OrderDetailItem, newItem: OrderDetailItem): Boolean {
        return if (oldItem is OrderDetailItem.Item && newItem is OrderDetailItem.Item) {
            oldItem.orderItem.uniqueSkuId == newItem.orderItem.uniqueSkuId
        } else {
            oldItem == newItem
        }
    }

    override fun areContentsTheSame(oldItem: OrderDetailItem, newItem: OrderDetailItem): Boolean {
        return oldItem == newItem
    }
}