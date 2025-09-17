package com.sdstore.auth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sdstore.feature_auth.R
import com.sdstore.feature_auth.databinding.FragmentSignupBinding
import com.sdstore.auth.viewmodels.AuthViewModel
import com.sdstore.auth.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.btnSignup.setOnClickListener {
            val name = binding.etSignupName.text.toString().trim()
            val email = binding.etSignupEmail.text.toString().trim()
            val password = binding.etSignupPassword.text.toString().trim()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerViewModel.saveUserName(name)
                authViewModel.signUpWithEmail(email, password, name)
            } else {
                Toast.makeText(context, getString(com.sdstore.R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.signUpState.collect { state ->
                    val isLoading = state is AuthViewModel.AuthState.Loading
                    binding.btnSignup.isEnabled = !isLoading

                    when (state) {
                        is AuthViewModel.AuthState.Success -> {
                            Toast.makeText(context, getString(com.sdstore.R.string.account_created_verify_email), Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_signUpFragment_to_verifyEmailFragment)
                            authViewModel.resetSignUpState()
                        }
                        is AuthViewModel.AuthState.Error -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            authViewModel.resetSignUpState()
                        }
                        else -> { /* Idle state mein kuch nahi karna */ }
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