package com.sdstore.orders.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sdstore.core.models.Order
import com.sdstore.feature_orders.databinding.ItemDeliveryBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.sdstore.core.R as coreR // Core module ke resources ke liye

class DeliveriesAdapter(private val onOrderClick: (Order) -> Unit) :
    ListAdapter<Order, DeliveriesAdapter.DeliveryViewHolder>(DeliveryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    inner class DeliveryViewHolder(private val binding: ItemDeliveryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            val context = itemView.context
            binding.root.setOnClickListener { onOrderClick(order) }

            when (order.status.lowercase(Locale.getDefault())) {
                "cancelled" -> {
                    binding.ivStatusIcon.setImageResource(coreR.drawable.ic_cancel)
                    binding.ivStatusIcon.setColorFilter(ContextCompat.getColor(context, coreR.color.red))
                }
                "delivered" -> {
                    binding.ivStatusIcon.setImageResource(coreR.drawable.ic_check_circle)
                    binding.ivStatusIcon.setColorFilter(ContextCompat.getColor(context, coreR.color.green))
                }
                else -> {
                    binding.ivStatusIcon.setImageResource(com.sdstore.feature_orders.R.drawable.ic_scheduled)
                    binding.ivStatusIcon.clearColorFilter()
                }
            }

            binding.tvDeliveryId.text = context.getString(coreR.string.order_id_prefix, order.orderId)
            binding.tvDeliveryStatus.text = order.status

            val formattedDate = order.createdAt?.let {
                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it)
            } ?: context.getString(coreR.string.date_not_available)
            binding.tvDeliveryDate.text = formattedDate

            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            binding.tvDeliveryAmount.text = format.format(order.totalPrice / 100.0)
        }
    }
}

class DeliveryDiffCallback : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}