package com.sdstore.orders.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sdstore.core.models.Order
import com.sdstore.core.utils.InvoiceGenerator
import com.sdstore.core.viewmodels.UiState
import com.sdstore.feature_orders.R
import com.sdstore.feature_orders.databinding.FragmentDeliveryDetailBinding
import com.sdstore.orders.viewmodels.DeliveryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DeliveryDetailFragment : Fragment() {

    private var _binding: FragmentDeliveryDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DeliveryDetailFragmentArgs by navArgs()
    private val deliveryViewModel: DeliveryViewModel by activityViewModels()

    private lateinit var orderDetailAdapter: OrderDetailAdapter
    private var orderForInvoice: Order? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                orderForInvoice?.let { downloadInvoice(it) }
            } else {
                Toast.makeText(context, getString(R.string.storage_permission_needed), Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeliveryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()

        val order = args.selectedOrder
        updateUi(order)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        orderDetailAdapter = OrderDetailAdapter(
            onCancelClick = { orderId ->
                val bottomSheet = DeliveryCancelConfirmationBottomSheet.newInstance(orderId)
                bottomSheet.show(parentFragmentManager, "CancelConfirmSheet")
            },
            onDownloadInvoiceClick = { order ->
                orderForInvoice = order
                checkStoragePermissionAndDownload()
            }
        )
        binding.deliveryDetailListView.adapter = orderDetailAdapter
    }

    private fun checkStoragePermissionAndDownload() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                orderForInvoice?.let { downloadInvoice(it) }
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                Toast.makeText(context, getString(R.string.storage_permission_needed), Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun downloadInvoice(order: Order) {
        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                InvoiceGenerator.createInvoice(requireContext(), order)
            }
            if (success) {
                Toast.makeText(context, getString(R.string.invoice_download_success), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, getString(R.string.invoice_download_failed), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUi(order: Order) {
        val detailItems = mutableListOf<OrderDetailItem>()
        detailItems.add(OrderDetailItem.Header(order))
        order.items.forEach { orderItem ->
            detailItems.add(OrderDetailItem.Item(orderItem))
        }
        detailItems.add(OrderDetailItem.Footer(order))

        orderDetailAdapter.submitList(detailItems)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                deliveryViewModel.orderUpdateState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            Toast.makeText(context, getString(R.string.order_cancelled_successfully), Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                            deliveryViewModel.resetOrderUpdateState()
                        }
                        is UiState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            deliveryViewModel.resetOrderUpdateState()
                        }
                        else -> { /* Idle ya Loading state mein kuch nahi karna */ }
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