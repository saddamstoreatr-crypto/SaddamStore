package com.sdstore.auth.register

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sdstore.R
import com.sdstore.auth.databinding.FragmentRegisterSuccessBinding
import com.sdstore.auth.viewmodels.RegisterViewModel
import com.sdstore.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterSuccessFragment : Fragment() {

    private var _binding: FragmentRegisterSuccessBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.outletName.value.isNullOrEmpty()) {
            binding.successText.text = getString(R.string.registration_successful)
        } else {
            binding.successText.text = getString(R.string.outlet_has_been_registered)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}