package com.sdstore.feature_orders.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdstore.feature_orders.databinding.BottomSheetDeliveryCancelConfirmationBinding
import com.sdstore.feature_orders.viewmodels.DeliveryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeliveryCancelConfirmationBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDeliveryCancelConfirmationBinding? = null
    private val binding get() = _binding!!

    private val deliveryViewModel: DeliveryViewModel by activityViewModels()
    private var orderIdToCancel: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderIdToCancel = arguments?.getString(ARG_ORDER_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetDeliveryCancelConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            val reason = binding.etCancellationReason.text.toString().trim()
            if (reason.isEmpty()) {
                Toast.makeText(context, "Barah karam order cancel karne ki wajah likhein.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            orderIdToCancel?.let {
                // --- BADLAO: Ab hum wajah bhi ViewModel ko bhej rahe hain ---
                deliveryViewModel.cancelOrder(it, reason)
                dismiss()
            }
        }
        binding.goBackButton.setOnClickListener {
            dismiss()
        }
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private const val ARG_ORDER_ID = "order_id"

        fun newInstance(orderId: String): DeliveryCancelConfirmationBottomSheet {
            val fragment = DeliveryCancelConfirmationBottomSheet()
            val args = Bundle().apply {
                putString(ARG_ORDER_ID, orderId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}