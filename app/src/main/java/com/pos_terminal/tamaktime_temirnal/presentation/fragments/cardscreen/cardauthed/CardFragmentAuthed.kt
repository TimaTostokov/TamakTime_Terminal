package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderItemFull
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardAuthedBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.OrderItemAdapter
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
@AndroidEntryPoint
class CardFragmentAuthed : Fragment() {

    private var binding: FragmentCardAuthedBinding by autoCleared()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: CardFragmentViewModel by activityViewModels()
    private lateinit var orderItemAdapter: OrderItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardAuthedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        val limit = viewModel.loadStudentLimit(studentId = 1)

        binding.buttonCancelOrder.setOnClickListener {
            viewModel.resetCardState()
        }

        binding.mrlBtnPay.setOnClickListener {
            viewModel.postOrder()
        }


        orderItemAdapter = OrderItemAdapter(object : OrderItemAdapter.OrderItemListener {
            override fun onAdd(orderItemFull: OrderItemFull) {
                sharedViewModel.addProductToOrder(orderItemFull.product!!)
            }

            override fun onRemove(orderItemFull: OrderItemFull) {
                sharedViewModel.removeProductFromOrder(orderItemFull)
            }
        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderItemAdapter
        }

        lifecycleScope.launch {
            sharedViewModel.orderItems.collect { orderItems ->
                orderItemAdapter.submitList(orderItems)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.student.collect { student ->
                student?.let {
                    binding.tvClientName.text = "${it.firstName} ${it.lastName}"
                    binding.tvClientBalance.text = it.balance
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.studentLimit.collect { limit ->
                limit?.let {
                    binding.tvClientTotalBalance.text = it
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.studentQR.collect { student ->
                student?.let {
                    binding.tvClientName.text = "${it.creator.firstName} ${it.creator.lastName}"
                    binding.tvClientBalance.text = it.creator.balance
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._totalPrice.collect {
                Log.d("TotalPriceObserver", "Total price updated: $it")
                binding.tvTotal.text = it.toString()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.cardState.collect { state ->
                    when (state) {
                        CardState.AUTHENTICATED -> {
                            binding.lytContent.visibility = View.VISIBLE
                            binding.lytOrderContent.visibility = View.GONE
                        }

                        CardState.ORDER -> {
                            binding.lytContent.visibility = View.GONE
                            binding.lytOrderContent.visibility = View.VISIBLE
                        }

                        CardState.ORDERING -> {
                            findNavController().navigate(R.id.action_cardFragmentAuthed_to_cardFragmentLoading)
                        }

                        CardState.INITIAL -> {
                            findNavController().navigate(R.id.action_cardFragmentAuthed_to_cardFragmentInitial)
                        }

                        else -> {
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.orderMap.collect { map ->
                    val items = map.values.toList()

                    if (viewModel.cardState.value == CardState.ORDER) {
                        map.values.let {
                            if ((viewModel.student.value?.balance?.toDouble()
                                    ?: 0.0) < viewModel._totalPrice.value
                            ) {
                                binding.tvTotal.setTextColor(requireContext().getColor(R.color.balance_error))
                                binding.tvTotalLabel.setTextColor(requireContext().getColor(R.color.balance_error))
                                binding.tvNotEnoughMoney.visibility = View.VISIBLE
                                binding.mrlBtnPay.isEnabled = false
                                binding.mrlBtnPay.isClickable = false
                                binding.mrlBtnPay.setBackgroundColor(requireContext().getColor(R.color.disabled_btn))
                            } else {
                                binding.tvTotal.setTextColor(requireContext().getColor(android.R.color.black))
                                binding.tvTotalLabel.setTextColor(requireContext().getColor(android.R.color.black))
                                binding.tvNotEnoughMoney.visibility = View.INVISIBLE
                                binding.mrlBtnPay.isEnabled = true
                                binding.mrlBtnPay.isClickable = true
                                binding.mrlBtnPay.setBackgroundColor(requireContext().getColor(R.color.primaryDark))
                            }
                        }
                    }
                }
            }
        }
    }

}