package com.sdstore.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.sdstore.databinding.FragmentEnterOtpBinding
import com.sdstore.main.MainActivity

class EnterOtpFragment : Fragment() {

    private var _binding: FragmentEnterOtpBinding? = null
    private val binding get() = _binding!!
    private val args: EnterOtpFragmentArgs by navArgs()
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.instructionSubtitleTextView.text = "Code sent to ${args.phoneNumber}"
        startTimer()
        setupListeners()
    }

    private fun setupListeners() {
        binding.resendCodeButton.setOnClickListener {
            // Abhi ke liye resend logic ko skip karte hain, aap baad mein add kar sakte hain
            Toast.makeText(context, "Resend functionality will be added later.", Toast.LENGTH_SHORT).show()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // OTP EditText ke liye listener
        binding.codeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 6) {
                    binding.progressBar.visibility = View.VISIBLE
                    verifyOtp(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun verifyOtp(otp: String) {
        try {
            val credential = PhoneAuthProvider.getCredential(args.verificationId, otp)
            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(context, "Verification ID mein masla hai. Dobara koshish karein.", Toast.LENGTH_LONG).show()
            Log.e("AUTH", "Credential error", e)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    // Sign in kamyab, MainActivity par jayein
                    Toast.makeText(context, "Login Kamyab!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    activity?.finish()
                } else {
                    // Sign in nakam
                    Toast.makeText(context, "OTP ghalat hai. Dobara koshish karein.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun startTimer() {
        binding.resendCodeButton.isEnabled = false
        countdownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.countdownTextView.text = "Resend code in $seconds s"
            }

            override fun onFinish() {
                binding.resendCodeButton.isEnabled = true
                binding.countdownTextView.text = "Resend code"
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer.cancel()
        _binding = null
    }
}