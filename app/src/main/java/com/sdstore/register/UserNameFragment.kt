package com.sdstore.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sdstore.R
import com.sdstore.databinding.FragmentUserNameBinding
import com.sdstore.viewmodels.RegisterViewModel
import com.sdstore.viewmodels.ViewModelFactory

class UserNameFragment : Fragment() {
    private var _binding: FragmentUserNameBinding? = null
    private val binding get() = _binding!!

    // activityViewModels کا استعمال کریں تاکہ رجسٹریشن کے تمام فریگمنٹس میں ایک ہی ViewModel استعمال ہو
    private val viewModel: RegisterViewModel by activityViewModels {
        ViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            if (name.isNotEmpty()) {
                // نام کو مشترکہ ViewModel میں محفوظ کریں
                viewModel.saveUserName(name)
                // اگلی اسکرین پر جائیں
                findNavController().navigate(R.id.action_userNameFragment_to_outletNameFragment)
            } else {
                Toast.makeText(context, "براہ کرم اپنا نام درج کریں", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            // رجسٹریشن کا عمل ختم کریں اور واپس جائیں
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}