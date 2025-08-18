package com.sdstore.main.deliveries.deliverydetail.deliverycancellation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdstore.databinding.BottomSheetDeliveryCancelConfirmationBinding

class DeliveryCancelConfirmationBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDeliveryCancelConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDeliveryCancelConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            // Delivery cancel karne ka logic yahan aayega
            dismiss()
        }
        binding.goBackButton.setOnClickListener {
            dismiss()
        }
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}