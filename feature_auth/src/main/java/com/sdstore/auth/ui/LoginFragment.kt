package com.sdstore.auth.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sdstore.R
import com.sdstore.auth.databinding.FragmentLoginBinding
import com.sdstore.auth.help.FilerHelpBottomSheet
import com.sdstore.auth.register.RegisterActivity
import com.sdstore.auth.viewmodels.AuthViewModel
import com.sdstore.core.data.Result
import com.sdstore.main.MainActivity
import com.sdstore.orders.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginWithEmail(email, password)
            } else {
                Toast.makeText(context, R.string.fill_all_fields, Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvGoToSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
        binding.tvHelpline.setOnClickListener {
            FilerHelpBottomSheet.newInstance().show(parentFragmentManager, "FilerHelpBottomSheet")
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loginState.collect { state ->
                        val isLoading = state is AuthViewModel.AuthState.Loading
                        binding.btnLogin.isEnabled = !isLoading

                        when (state) {
                            is AuthViewModel.AuthState.Success -> {
                                checkUserProfile()
                                viewModel.resetLoginState()
                            }
                            is AuthViewModel.AuthState.Error -> {
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                                viewModel.resetLoginState()
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    viewModel.resetEmailState.collect { success ->
                        success?.let {
                            val message = if (it) getString(R.string.password_reset_link_sent) else getString(R.string.failed_to_send_reset_link)
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            viewModel.resetPasswordState()
                        }
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
                        navigateTo(MainActivity::class.java)
                    } else {
                        navigateTo(RegisterActivity::class.java)
                    }
                }
                is Result.Error -> {
                    navigateTo(RegisterActivity::class.java)
                }
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(activity, activityClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        activity?.finish()
    }


    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(com.sdstore.auth.R.layout.dialog_reset_password, null)
        val emailEditText = dialogView.findViewById<EditText>(com.sdstore.auth.R.id.et_reset_email)

        builder.setView(dialogView)
            .setTitle(getString(R.string.reset_password_title))
            .setMessage(getString(R.string.reset_password_message))
            .setPositiveButton(getString(R.string.send_link)) { dialog, _ ->
                val email = emailEditText.text.toString().trim()
                if (email.isNotEmpty()) {
                    viewModel.sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(context, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}