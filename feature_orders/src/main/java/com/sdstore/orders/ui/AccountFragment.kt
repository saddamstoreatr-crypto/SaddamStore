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
import com.sdstore.auth.help.FilerHelpBottomSheet
import com.sdstore.core.models.User
import com.sdstore.orders.databinding.FragmentAccountBinding
import com.sdstore.orders.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import perfetto.protos.UiState

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
        binding.helplineButton.setOnClickListener {
            FilerHelpBottomSheet.newInstance().show(parentFragmentManager, "FilerHelpBottomSheet")
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    userViewModel.userState.collect { state ->
                        when (state) {
                            is UserViewModel.UiState.Success -> {
                                currentUser = state.data
                                updateUi(state.data)
                            }
                            is UserViewModel.UiState.Error -> {
                                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            }
                            else -> { /* Do nothing for Idle or Loading */ }
                        }
                    }
                }

                launch {
                    userViewModel.profileUpdateStatus.collect { state ->
                        if (state is UserViewModel.UiState.Success) {
                            Toast.makeText(context, getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show()
                            userViewModel.resetProfileUpdateStatus()
                        } else if (state is UserViewModel.UiState.Error) {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            userViewModel.resetProfileUpdateStatus()
                        }
                    }
                }

                launch {
                    userViewModel.feedbackStatus.collect { state ->
                        if (state is UserViewModel.UiState.Success) {
                            Toast.makeText(context, getString(R.string.feedback_submitted_successfully), Toast.LENGTH_SHORT).show()
                            userViewModel.resetFeedbackStatus()
                        } else if (state is UserViewModel.UiState.Error) {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            userViewModel.resetFeedbackStatus()
                        }
                    }
                }

                launch {
                    userViewModel.loggedOutEvent.collect { hasLoggedOut ->
                        if (hasLoggedOut) {
                            val intent = Intent(activity, AuthActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            activity?.finish()
                            userViewModel.onLogoutEventHandled()
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
        val feedbackEditText = dialogView.wByIdfindVie<EditText>(R.id.et_feedback)

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