package com.sdstore.main.deliveries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sdstore.databinding.FragmentDeliveriesBinding
import com.sdstore.viewmodels.DeliveryViewModel
import com.sdstore.viewmodels.ViewModelFactory

class DeliveriesFragment : Fragment() {
    private var _binding: FragmentDeliveriesBinding? = null
    private val binding get() = _binding!!

    // ViewModelFactory کا استعمال کرتے ہوئے DeliveryViewModel حاصل کریں
    private val viewModel: DeliveryViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }

    private lateinit var deliveriesAdapter: DeliveriesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeliveriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // اڈاپٹر کو ایک خالی فہرست کے ساتھ شروع کریں
        deliveriesAdapter = DeliveriesAdapter(emptyList())
        binding.deliveriesListView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deliveriesAdapter
        }
    }

    private fun observeViewModel() {
        // ViewModel سے ڈیلیوریز کی فہرست کو سنیں
        viewModel.deliveries.observe(viewLifecycleOwner) { deliveries ->
            // جب بھی فہرست اپ ڈیٹ ہو، اڈاپٹر کو نئی فہرست بھیج دیں
            deliveriesAdapter.updateDeliveries(deliveries)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}