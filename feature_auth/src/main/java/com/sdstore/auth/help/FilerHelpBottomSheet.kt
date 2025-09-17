package com.sdstore.auth.help

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sdstore.R
import com.sdstore.feature_auth.databinding.BottomSheetFilerHelpBinding

class FilerHelpBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFilerHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilerHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val helplineNumber = getString(R.string.helpline_whatsapp_number)
        val helpDetailsText = getString(R.string.helpline_details, helplineNumber)
        binding.textViewWhatsappBottomSheet.text = helpDetailsText

        binding.whatsappButton.setOnClickListener {
            openWhatsApp()
        }
        binding.backButton.setOnClickListener {
            dismiss()
        }
    }

    private fun openWhatsApp() {
        val rawPhoneNumber = getString(R.string.helpline_whatsapp_number)
        val cleanedPhoneNumber = rawPhoneNumber.replace("[\\s+-]".toRegex(), "")
        val message = "ہیلو، مجھے مدد کی ضرورت ہے۔"

        try {
            requireActivity().packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanedPhoneNumber&text=${Uri.encode(message)}")
            }
            startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(context, "آپ کے فون میں واٹس ایپ انسٹال نہیں ہے۔", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "کوئی مسئلہ پیش آ گیا ہے۔", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): FilerHelpBottomSheet {
            return FilerHelpBottomSheet()
        }
    }
}