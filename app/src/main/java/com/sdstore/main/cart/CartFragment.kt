package com.sdstore.main.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sdstore.databinding.FragmentCartBinding
import com.sdstore.viewmodels.CartViewModel
import com.sdstore.viewmodels.ViewModelFactory

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels { ViewModelFactory(requireActivity().application) }
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // بٹن کے کلک پر ViewModel کا فنکشن کال کریں
        binding.btnPlaceOrder.setOnClickListener {
            viewModel.placeOrder()
        }
    }

    private fun setupRecyclerView() {
        // ... (پچھلا کوڈ)
    }

    private fun observeViewModel() {
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            // ... (پچھلا کوڈ)
        }

        // آرڈر کے نتیجے کو سنیں
        viewModel.orderPlaced.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "آپ کا آرڈر کامیابی سے دے دیا گیا ہے!", Toast.LENGTH_LONG).show()
                // TODO: صارف کو "میرے آرڈرز" کی اسکرین پر بھیجیں
            } else {
                Toast.makeText(context, "آرڈر دینے میں مسئلہ ہوا", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}