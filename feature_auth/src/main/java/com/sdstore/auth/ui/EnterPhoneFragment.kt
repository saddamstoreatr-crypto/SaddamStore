package com.sdstore.auth.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sdstore.R
import com.sdstore.feature_auth.databinding.FragmentEnterPhoneBinding
import com.sdstore.auth.help.FilerHelpBottomSheet
import java.util.concurrent.TimeUnit

class EnterPhoneFragment : Fragment() {

    private var _binding: FragmentEnterPhoneBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setupListeners()
        updateButtonState(false)
    }

    private fun setupListeners() {
        binding.phoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateButtonState(s.toString().length == 10)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.submitButton.setOnClickListener {
            val phoneNumber = binding.phoneNumberEditText.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                val fullPhoneNumber = "+92$phoneNumber"
                startPhoneNumberVerification(fullPhoneNumber)
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvHelpline.setOnClickListener {
            FilerHelpBottomSheet.newInstance().show(parentFragmentManager, "FilerHelpBottomSheet")
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        setLoading(true)
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            setLoading(false)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (isAdded && _binding != null) {
                setLoading(false)
                Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            if (isAdded && _binding != null) {
                setLoading(false)
                val fullPhoneNumber = "+92${binding.phoneNumberEditText.text.toString()}"
                val action = EnterPhoneFragmentDirections.actionEnterPhoneFragmentToEnterOtpFragment(
                    fullPhoneNumber,
                    verificationId,
                    token
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun updateButtonState(isEnabled: Boolean) {
        binding.submitButton.isEnabled = isEnabled
    }

    private fun setLoading(isLoading: Boolean) {
        if(isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvConnectingStatus.visibility = View.VISIBLE
            binding.submitButton.text = ""
            updateButtonState(false)
        } else {
            binding.progressBar.visibility = View.GONE
            binding.tvConnectingStatus.visibility = View.GONE
            binding.submitButton.text = getString(R.string.next)
            updateButtonState(binding.phoneNumberEditText.text.length == 10)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}