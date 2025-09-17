package com.sdstore.feature_auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sdstore.R
import com.sdstore.feature_auth.databinding.FragmentOutletNameBinding
import com.sdstore.feature_auth.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OutletNameFragment : Fragment() {
    private var _binding: FragmentOutletNameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOutletNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            val outletName = binding.nameEditText.text.toString().trim()
            if (outletName.isNotEmpty()) {
                viewModel.saveOutletName(outletName)
                findNavController().navigate(com.sdstore.feature_auth.R.id.action_outletNameFragment_to_enterUserPhoneFragment)
            } else {
                Toast.makeText(context, getString(R.string.please_enter_outlet_name_or_skip), Toast.LENGTH_SHORT).show()
            }
        }

        binding.skipButton.setOnClickListener {
            viewModel.saveOutletName("")
            findNavController().navigate(com.sdstore.feature_auth.R.id.action_outletNameFragment_to_enterUserPhoneFragment)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}