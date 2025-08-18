package com.sdstore.main.deliveries

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sdstore.R
import com.sdstore.databinding.ItemDeliveryBinding
import com.sdstore.models.Delivery
import java.text.NumberFormat
import java.util.Locale

class DeliveriesAdapter(private var deliveries: List<Delivery>) : RecyclerView.Adapter<DeliveriesAdapter.DeliveryViewHolder>() {

    inner class DeliveryViewHolder(val binding: ItemDeliveryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun getItemCount(): Int = deliveries.size

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        val delivery = deliveries[position]
        holder.binding.apply {
            tvDeliveryDate.text = "ڈیلیوری: ${delivery.date}"
            tvDeliveryStatus.text = delivery.status

            val format = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
            format.maximumFractionDigits = 0
            tvDeliveryAmount.text = format.format(delivery.totalAmount)

            when (delivery.status) {
                "مکمل" -> ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
                "ناکام" -> ivStatusIcon.setImageResource(R.drawable.ic_cancel)
                "شیڈیول شدہ" -> ivStatusIcon.setImageResource(R.drawable.ic_scheduled)
                else -> ivStatusIcon.setImageDrawable(null)
            }
        }
    }

    fun updateDeliveries(newDeliveries: List<Delivery>) {
        deliveries = newDeliveries
        notifyDataSetChanged()
    }
}