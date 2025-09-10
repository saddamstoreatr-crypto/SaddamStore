package com.sdstore.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sdstore.auth.R
import com.sdstore.auth.databinding.FragmentEnterUserPhoneBinding
import com.sdstore.auth.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterUserPhoneFragment : Fragment() {

    private var _binding: FragmentEnterUserPhoneBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterUserPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = Firebase.auth.currentUser
        if (!currentUser?.phoneNumber.isNullOrEmpty()) {
            viewModel.saveUserPhone(currentUser!!.phoneNumber!!)
            findNavController().navigate(com.sdstore.auth.R.id.action_enterUserPhoneFragment_to_outletLocationFragment)
            return
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            val phoneNumber = binding.phoneEditText.text.toString().trim()
            if (phoneNumber.length == 11 && phoneNumber.startsWith("03")) {
                viewModel.saveUserPhone(phoneNumber)
                findNavController().navigate(com.sdstore.auth.R.id.action_enterUserPhoneFragment_to_outletLocationFragment)
            } else {
                Toast.makeText(context, getString(R.string.please_enter_phone_number), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}