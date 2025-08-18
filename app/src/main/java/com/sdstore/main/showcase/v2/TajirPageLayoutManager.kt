package com.sdstore.main.showcase.v2

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TajirPageLayoutManager(
    context: Context,
    private val adapter: RecyclerView.Adapter<*>,
    spanCount: Int
) : GridLayoutManager(context, spanCount) {

    init {
        spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Yahan har item type ke liye span size define karne ka logic aayega
                // For example:
                // return if (adapter.getItemViewType(position) == SOME_FULL_WIDTH_TYPE) spanCount else 1
                return 1 // Default
            }
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: Exception) {
            // Log the exception
            e.printStackTrace()
        }
    }
}