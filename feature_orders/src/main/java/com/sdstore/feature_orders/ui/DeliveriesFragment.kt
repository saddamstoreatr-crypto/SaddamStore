package com.sdstore.feature_orders.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdstore.core.viewmodels.UiState
import com.sdstore.feature_orders.databinding.FragmentDeliveriesBinding
import com.sdstore.feature_orders.viewmodels.DeliveryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeliveriesFragment : Fragment() {

    private var _binding: FragmentDeliveriesBinding? = null
    private val binding get() = _binding!!

    private val deliveryViewModel: DeliveryViewModel by activityViewModels()
    private lateinit var deliveriesAdapter: DeliveriesAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeliveriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        deliveryViewModel.refreshOrders()
    }

    private fun setupRecyclerView() {
        layoutManager = LinearLayoutManager(context)
        binding.deliveriesListView.layoutManager = layoutManager
        deliveriesAdapter = DeliveriesAdapter { order ->
            val action = DeliveriesFragmentDirections.actionDeliveriesFragmentToDeliveryDetailFragment(order)
            findNavController().navigate(action)
        }
        binding.deliveriesListView.adapter = deliveriesAdapter

        binding.deliveriesListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    deliveryViewModel.loadMoreOrders()
                }
            }
        })
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                deliveryViewModel.ordersState.collect { state ->
                    binding.progressBar.isVisible = state is UiState.Loading

                    when (state) {
                        is UiState.Success -> {
                            binding.tvEmptyOrders.isVisible = state.data.isEmpty()
                            deliveriesAdapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            binding.tvEmptyOrders.text = state.message
                            binding.tvEmptyOrders.isVisible = true
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                        else -> { /* Do nothing for Idle or Loading */ }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}