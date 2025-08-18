package com.sdstore.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sdstore.databinding.FragmentOutletNameBinding

class OutletNameFragment : Fragment() {
    private var _binding: FragmentOutletNameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOutletNameBinding.inflate(inflater, container, false)
        binding.submitButton.setOnClickListener {
            val outletName = binding.nameEditText.text.toString()
            // TODO: Save outlet name via ViewModel and navigate to next screen
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}