package com.sdstore.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // درست امپورٹ
import com.sdstore.databinding.FragmentSearchBinding
import com.sdstore.main.page.ProductAdapter
import com.sdstore.viewmodels.CartViewModel // CartViewModel کا درست امپورٹ
import com.sdstore.viewmodels.SearchViewModel
import com.sdstore.viewmodels.ViewModelFactory

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels { ViewModelFactory(requireActivity().application) }
    // یہاں غلطی درست کی گئی ہے: viewmodels کی بجائے viewModels()
    private val cartViewModel: CartViewModel by viewModels { ViewModelFactory(requireActivity().application) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            binding.rvSearchResults.adapter = ProductAdapter(
                products = results,
                onAddToCartClick = { sku ->
                    cartViewModel.addToCart(sku)
                    Toast.makeText(context, "${sku.name} کارٹ میں شامل کر دیا گیا", Toast.LENGTH_SHORT).show()
                },
                onNotifyMeClick = { sku ->
                    Toast.makeText(context, "اطلاع دی جائے گی", Toast.LENGTH_SHORT).show()
                },
                onItemClick = { sku ->
                    // TODO: Open ItemDetailDialog
                }
            )
        }
    }
}