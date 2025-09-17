package com.sdstore.orders.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sdstore.core.models.User
import com.sdstore.core.viewmodels.UiState
import com.sdstore.core.viewmodels.UserViewModel
import com.sdstore.feature_orders.R
import com.sdstore.feature_orders.databinding.FragmentAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            userViewModel.logout()
        }
        binding.btnEditProfile.setOnClickListener {
            currentUser?.let { user ->
                showEditProfileDialog(user)
            }
        }
        binding.feedbackButton.setOnClickListener {
            showFeedbackDialog()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    userViewModel.userState.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                currentUser = state.data
                                updateUi(state.data)
                            }
                            is UiState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            }
                            else -> { /* Do nothing for Idle or Loading */ }
                        }
                    }
                }
            }
        }
    }

    private fun updateUi(user: User) {
        binding.name.text = user.name
        binding.outletName.text = user.outletName.ifEmpty { getString(R.string.not_provided) }
        binding.phoneNumber.text = user.phone
    }

    private fun showFeedbackDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_feedback, null)
        val feedbackEditText = dialogView.findViewById<EditText>(R.id.et_feedback)

        builder.setView(dialogView)
            .setTitle(getString(R.string.feedback_suggestion))
            .setPositiveButton(getString(R.string.submit)) { dialog, _ ->
                val feedbackText = feedbackEditText.text.toString().trim()
                if (feedbackText.isNotEmpty()) {
                    userViewModel.submitFeedback(feedbackText)
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
        builder.create().show()
    }

    private fun showEditProfileDialog(currentUser: User) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_edit_profile, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.et_edit_name)
        val outletNameEditText = dialogView.findViewById<EditText>(R.id.et_edit_outlet_name)

        nameEditText.setText(currentUser.name)
        outletNameEditText.setText(currentUser.outletName)

        builder.setView(dialogView)
            .setTitle(getString(R.string.edit_profile))
            .setPositiveButton(getString(R.string.save)) { dialog, _ ->
                val newName = nameEditText.text.toString().trim()
                val newOutletName = outletNameEditText.text.toString().trim()

                if (newName.isNotEmpty()) {
                    userViewModel.updateUserProfile(newName, newOutletName)
                } else {
                    Toast.makeText(context, getString(R.string.name_cannot_be_empty), Toast.LENGTH_SHORT).show()
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