package com.sdstore.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.sdstore.databinding.FragmentEnterPhoneBinding
import java.util.concurrent.TimeUnit

class EnterPhoneFragment : Fragment() {

    private var _binding: FragmentEnterPhoneBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var storedVerificationId: String? = null

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
        binding.submitButton.setOnClickListener {
            sendVerificationCode()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.phoneNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val phone = s.toString()
                val isValid = phone.length == 10 && phone.startsWith("3")
                updateButtonState(isValid)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun sendVerificationCode() {
        val phoneNumber = "+92" + binding.phoneNumberEditText.text.toString()
        binding.progressBar.visibility = View.VISIBLE
        updateButtonState(false)

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
            Log.d("AUTH", "onVerificationCompleted:$credential")
            binding.progressBar.visibility = View.GONE
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w("AUTH", "onVerificationFailed", e)
            Toast.makeText(context, "OTP bhejne mein masla hua: ${e.message}", Toast.LENGTH_LONG).show()
            binding.progressBar.visibility = View.GONE
            updateButtonState(true)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("AUTH", "onCodeSent:$verificationId")
            storedVerificationId = verificationId
            val phoneNumber = "+92" + binding.phoneNumberEditText.text.toString()
            val action = EnterPhoneFragmentDirections.actionEnterPhoneFragmentToEnterOtpFragment(phoneNumber, verificationId)
            findNavController().navigate(action)
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun updateButtonState(isEnabled: Boolean) {
        binding.submitButton.isEnabled = isEnabled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}