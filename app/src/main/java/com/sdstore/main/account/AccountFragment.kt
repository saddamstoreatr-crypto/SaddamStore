package com.sdstore.main.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sdstore.databinding.FragmentAccountBinding
import com.sdstore.viewmodels.AuthViewModel
import com.sdstore.viewmodels.UserViewModel
import com.sdstore.viewmodels.ViewModelFactory

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels { ViewModelFactory(requireActivity().application) }
    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(requireActivity().application) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.name.text = it.name
                binding.outletName.text = it.storeName
                binding.phoneNumber.text = it.phone
            }
        }

        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            // TODO: Navigate user to AuthActivity and finish MainActivity
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}