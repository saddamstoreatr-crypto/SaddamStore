package com.sdstore.orders.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sdstore.core.models.Order
import com.sdstore.feature_orders.R
import com.sdstore.feature_orders.databinding.ItemDeliveryBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DeliveriesAdapter(private val onItemClick: (Order) -> Unit) :
    ListAdapter<Order, DeliveriesAdapter.DeliveryViewHolder>(DeliveryDiffCallback()) {

    inner class DeliveryViewHolder(private val binding: ItemDeliveryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            val context = itemView.context
            val date = order.createdAt
            val formattedDate = if (date != null) {
                SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(date)
            } else {
                context.getString(R.string.order_date_unavailable)
            }
            binding.tvDeliveryDate.text = context.getString(R.string.order_prefix, formattedDate)
            binding.tvDeliveryStatus.text = order.status

            when (order.status.lowercase()) {
                "pending" -> binding.ivStatusIcon.setImageResource(R.drawable.ic_scheduled)
                "cancelled" -> binding.ivStatusIcon.setImageResource(R.drawable.ic_cancel)
                else -> binding.ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
            }

            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            binding.tvDeliveryAmount.text = format.format(order.totalPrice / 100.0)

            if (order.status.equals("Cancelled", ignoreCase = true) && !order.cancellationReason.isNullOrEmpty()) {
                binding.tvCancellationReason.visibility = View.VISIBLE
                binding.tvCancellationReason.text = context.getString(R.string.cancellation_reason_prefix, order.cancellationReason)
            } else {
                binding.tvCancellationReason.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClick(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding =
            ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(getItem(position))
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