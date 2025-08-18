package com.sdstore.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sdstore.R
import com.sdstore.databinding.FragmentOutletLocationBinding
import com.sdstore.viewmodels.RegisterViewModel
import com.sdstore.viewmodels.ViewModelFactory

class OutletLocationFragment : Fragment() {
    private var _binding: FragmentOutletLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by activityViewModels { ViewModelFactory(requireActivity().application) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOutletLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerLocationButton.setOnClickListener {
            viewModel.saveRegistrationData()
        }

        viewModel.registrationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigate(R.id.action_outletLocationFragment_to_registerSuccessFragment)
            }
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