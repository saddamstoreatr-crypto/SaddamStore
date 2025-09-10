package com.sdstore.auth.ui

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sdstore.R
import com.sdstore.auth.databinding.FragmentEnterOtpBinding
import com.sdstore.auth.register.RegisterActivity
import com.sdstore.auth.viewmodels.AuthViewModel
import com.sdstore.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class EnterOtpFragment : Fragment() {

    private var _binding: FragmentEnterOtpBinding? = null
    private val binding get() = _binding!!
    private val args: EnterOtpFragmentArgs by navArgs()
    private var countdownTimer: CountDownTimer? = null

    private val viewModel: AuthViewModel by activityViewModels()

    private var currentVerificationId: String = ""
    private var currentResendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEnterOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentVerificationId = args.verificationId
        currentResendToken = args.resendToken

        binding.instructionTextView.text = getString(R.string.enter_otp_instruction, args.phoneNumber)
        setupOtpEditTexts()
        startTimer()
        setupObservers()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.resendCodeButton.setOnClickListener {
            currentResendToken?.let { token ->
                resendVerificationCode(args.phoneNumber, token)
            }
        }

        binding.btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken) {
        binding.progressBar.visibility = View.VISIBLE
        binding.resendCodeButton.isEnabled = false
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.otpVerificationState.collect { state ->
                    when (state) {
                        is AuthViewModel.OtpVerificationState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnVerifyOtp.isEnabled = false
                        }
                        is AuthViewModel.OtpVerificationState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(context, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                            if (state.isNewUser) {
                                navigateTo(RegisterActivity::class.java)
                            } else {
                                navigateTo(MainActivity::class.java)
                            }
                            viewModel.resetOtpState()
                        }
                        is AuthViewModel.OtpVerificationState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnVerifyOtp.isEnabled = true
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetOtpState()
                        }
                        null -> {
                            // idle state
                        }
                    }
                }
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(activity, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("AUTH", "onVerificationCompleted: $credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w("AUTH", "onVerificationFailed", e)
            binding.progressBar.visibility = View.GONE
            Toast.makeText(context, getString(R.string.otp_send_fail, e.localizedMessage), Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("AUTH", "New code sent: $verificationId")
            currentVerificationId = verificationId
            currentResendToken = token
            binding.progressBar.visibility = View.GONE
            Toast.makeText(context, getString(R.string.new_code_sent), Toast.LENGTH_SHORT).show()
            startTimer()
        }
    }

    private fun setupOtpEditTexts() {
        val otpWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    when (requireActivity().currentFocus) {
                        binding.otp1 -> binding.otp2.requestFocus()
                        binding.otp2 -> binding.otp3.requestFocus()
                        binding.otp3 -> binding.otp4.requestFocus()
                        binding.otp4 -> binding.otp5.requestFocus()
                        binding.otp5 -> binding.otp6.requestFocus()
                        binding.otp6 -> verifyOtp()
                    }
                }
                checkIfOtpIsComplete()
            }
        }

        binding.otp1.addTextChangedListener(otpWatcher)
        binding.otp2.addTextChangedListener(otpWatcher)
        binding.otp3.addTextChangedListener(otpWatcher)
        binding.otp4.addTextChangedListener(otpWatcher)
        binding.otp5.addTextChangedListener(otpWatcher)
        binding.otp6.addTextChangedListener(otpWatcher)
    }

    private fun checkIfOtpIsComplete() {
        val otp = "${binding.otp1.text}${binding.otp2.text}${binding.otp3.text}${binding.otp4.text}${binding.otp5.text}${binding.otp6.text}"
        binding.btnVerifyOtp.isEnabled = otp.length == 6
    }

    private fun verifyOtp() {
        if (!binding.btnVerifyOtp.isEnabled) return

        val otp = "${binding.otp1.text}${binding.otp2.text}${binding.otp3.text}${binding.otp4.text}${binding.otp5.text}${binding.otp6.text}"
        if (otp.length == 6) {
            val credential = PhoneAuthProvider.getCredential(currentVerificationId, otp)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        viewModel.signInWithPhoneAuthCredential(credential)
    }

    private fun startTimer() {
        countdownTimer?.cancel()
        binding.resendCodeButton.isEnabled = false
        binding.countdownTextView.visibility = View.VISIBLE
        countdownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.countdownTextView.text = getString(R.string.resend_code_in, millisUntilFinished / 1000)
            }
            override fun onFinish() {
                binding.resendCodeButton.isEnabled = true
                binding.countdownTextView.visibility = View.GONE
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
        _binding = null
    }
}