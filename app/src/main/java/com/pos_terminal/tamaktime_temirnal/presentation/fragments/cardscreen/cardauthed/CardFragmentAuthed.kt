package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.Extensions.formatPrice
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardAuthedBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.OrderItemAdapter
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardFragmentAuthed : Fragment() {

    private var binding: FragmentCardAuthedBinding by autoCleared()

    private lateinit var orderItemAdapter: OrderItemAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val viewModel: CardFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardAuthedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderItemAdapter = OrderItemAdapter(object : OrderItemAdapter.OrderItemListener {
            override fun onAdd(product: Product) {
                sharedViewModel.addProductToOrder(product)
            }

            override fun onRemove(product: Product) {
                sharedViewModel.removeProductFromOrder(product)
            }

            override fun onDelete(product: Product) {
                sharedViewModel.deleteProductFromOrder(product)
            }
        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderItemAdapter
        }

        setupObservers()
        viewModel.loadStudentLimit(studentId = 1)

        binding.buttonCancelOrder.setOnClickListener {
            viewModel.resetCardState()
            sharedViewModel.resetOrder()
        }

        binding.mrlBtnPay.setOnClickListener {
            viewModel.postOrder(sharedViewModel.orderItems.value)
        }

        lifecycleScope.launch {
            sharedViewModel.orderItems.collect { orderItems ->
                orderItemAdapter.submitList(orderItems)
                val totalPrice = sharedViewModel.totalPrice.value
                viewModel.checkStudentLimit(totalPrice)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.student.collect { student ->
                student?.let {
                    binding.tvClientName.text = "${it.firstName} ${it.lastName}"
                    binding.tvClientBalance.text =
                        it.balance?.let { it1 -> formatPrice(it1.toDouble()) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.studentLimit.collect { limit ->
                limit?.let {
                    binding.tvClientTotalBalance.text = formatPrice(it.toDouble())
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

        lifecycleScope.launch {
            sharedViewModel.totalPrice.collect { totalPrice ->
                binding.tvTotal.text = formatPrice(totalPrice)
            }
        }

        lifecycleScope.launch {
            sharedViewModel.orderItems.collect { orderItems ->
                orderItemAdapter.submitList(orderItems)
                val totalPrice = sharedViewModel.totalPrice.value
                val balance = viewModel.student.value?.balance?.toDoubleOrNull() ?: 0.0
                val limit = viewModel.studentLimit.value?.toDoubleOrNull() ?: Double.MAX_VALUE

                val canPay = balance >= totalPrice
                val withinLimit = totalPrice <= limit

                if (!canPay || !withinLimit) {
                    binding.tvTotal.setTextColor(requireContext().getColor(R.color.balance_error))
                    binding.tvTotalLabel.setTextColor(requireContext().getColor(R.color.balance_error))
                    binding.tvNotEnoughMoney.visibility = View.VISIBLE
                    binding.mrlBtnPay.isEnabled = false
                    binding.mrlBtnPay.isClickable = false
                    binding.mrlBtnPay.setBackgroundColor(requireContext().getColor(R.color.disabled_btn))

                    binding.tvNotEnoughMoney.text = when {
                        !canPay -> "Недостаточно средств для оплаты"
                        !withinLimit -> "Превышен лимит заказа"
                        else -> "Недостаточно средств для дальнейшей оплаты"
                    }
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