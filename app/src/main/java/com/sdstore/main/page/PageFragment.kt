package com.sdstore.main.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sdstore.databinding.FragmentPageBinding
import com.sdstore.main.itemdetail.ItemDetailDialog
import com.sdstore.models.Sku
import com.sdstore.viewmodels.ViewModelFactory
import androidx.recyclerview.widget.GridLayoutManager

class PageFragment : Fragment() {
    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PageViewModel by viewModels { ViewModelFactory(requireActivity().application) }
    // CartViewModel کو بھی یہاں شامل کریں تاکہ 'Add to Cart' کام کر سکے
    private val cartViewModel: com.sdstore.viewmodels.CartViewModel by viewModels { ViewModelFactory(requireActivity().application) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemsListView.layoutManager = GridLayoutManager(context, 2)

        viewModel.products.observe(viewLifecycleOwner) { productList ->
            binding.itemsListView.adapter = ProductAdapter(
                products = productList,
                onAddToCartClick = { sku ->
                    cartViewModel.addToCart(sku)
                    Toast.makeText(context, "${sku.name} کارٹ میں شامل کر دیا گیا", Toast.LENGTH_SHORT).show()
                },
                onNotifyMeClick = { sku ->
                    Toast.makeText(context, "اطلاع دی جائے گی", Toast.LENGTH_SHORT).show()
                },
                onItemClick = { sku ->
                    showItemDetailDialog(sku)
                }
            )
        }
    }

    private fun showItemDetailDialog(sku: Sku) {
        val dialog = ItemDetailDialog()
        val args = Bundle()
        args.putParcelable("selected_sku", sku)
        dialog.arguments = args
        dialog.show(childFragmentManager, "ItemDetailDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}