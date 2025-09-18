package com.sdstore.feature_auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sdstore.feature_auth.R
import com.sdstore.feature_auth.databinding.FragmentUserNameBinding
import com.sdstore.feature_auth.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserNameFragment : Fragment() {
    private var _binding: FragmentUserNameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.userName?.isNotEmpty() == true) {
            findNavController().navigate(com.sdstore.feature_auth.R.id.action_userNameFragment_to_outletNameFragment)
            return
        }

        binding.submitButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.saveUserName(name)
                findNavController().navigate(com.sdstore.feature_auth.R.id.action_userNameFragment_to_outletNameFragment)
            } else {
                Toast.makeText(context, getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}