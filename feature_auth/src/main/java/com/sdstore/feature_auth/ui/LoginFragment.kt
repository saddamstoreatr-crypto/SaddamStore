package com.sdstore.feature_auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sdstore.feature_auth.R
import com.sdstore.feature_auth.databinding.FragmentLoginBinding
import com.sdstore.feature_auth.viewmodels.AuthViewModel
import com.sdstore.core.data.Result
import com.sdstore.core.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.loginWithEmail(email, password)
            } else {
                Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            // Reset password dialog logic
        }

        binding.tvGoToSignup.setOnClickListener {
            findNavController().navigate(com.sdstore.R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginState.collect { state ->
                    binding.progressBar.isVisible = state is AuthViewModel.AuthState.Loading
                    binding.btnLogin.isEnabled = state !is AuthViewModel.AuthState.Loading

                    when (state) {
                        is AuthViewModel.AuthState.Success -> {
                            checkUserProfile()
                            authViewModel.resetLoginState()
                        }
                        is AuthViewModel.AuthState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                        else -> { /* Loading and Idle states are handled by visibility binding */ }
                    }
                }
            }
        }
    }

    private fun checkUserProfile() {
        lifecycleScope.launch {
            when (val result = userViewModel.checkUserStatus()) {
                is Result.Success -> {
                    val user = result.data
                    if (user != null && user.name.isNotEmpty()) {
                        // MainActivity app module mein hai, isliye iska Intent aese banega
                        val intent = Intent(requireActivity(), Class.forName("com.sdstore.main.MainActivity"))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        // Registration process par bhejein
                        val intent = Intent(requireActivity(), Class.forName("com.sdstore.feature_auth.register.RegisterActivity"))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
                is Result.Error -> {
                    Toast.makeText(context, "Failed to check user profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}