package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderItemFull
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardAuthedBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.OrderItemAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardFragmentAuthed : Fragment() {

    private var binding: FragmentCardAuthedBinding by autoCleared()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var orderItemAdapter: OrderItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardAuthedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderItemAdapter = OrderItemAdapter(object : OrderItemAdapter.OrderItemListener {
            override fun onAdd(orderItemFull: OrderItemFull) {
                sharedViewModel.addProductToOrder(orderItemFull.product!!)
            }

            override fun onRemove(orderItemFull: OrderItemFull) {
                sharedViewModel.removeProductFromOrder(orderItemFull.product!!)
            }

            override fun onDelete(orderItemFull: OrderItemFull) {
                sharedViewModel.removeProductFromOrder(orderItemFull.product!!)
            }
        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderItemAdapter
        }
        lifecycleScope.launchWhenStarted {
            sharedViewModel.orderItems.collect { orderItems ->
                orderItemAdapter.submitList(orderItems)
            }
        }

    }
}
