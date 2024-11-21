package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pos_terminal.tamaktime_temirnal.R
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.Extensions
import com.pos_terminal.tamaktime_temirnal.common.Extensions.formatPrice
import com.pos_terminal.tamaktime_temirnal.common.UiState
import com.pos_terminal.tamaktime_temirnal.common.autoCleared
import com.pos_terminal.tamaktime_temirnal.data.remote.apiservice.DocsService
import com.pos_terminal.tamaktime_temirnal.data.remote.model.documents.DocumentResponse
import com.pos_terminal.tamaktime_temirnal.data.remote.model.documents.LineRequest
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.databinding.FragmentCardAuthedBinding
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.OrderItemAdapter
import com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel.CardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
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
            postOrdering()
            viewModel.ordering()
            updateDocument()
            Log.d("arsenchik12","${sharedViewModel.orderItems.value}")
            Log.d("arsenchik123","${updateDocument()}")
            val currentDestination = findNavController().currentDestination
            Log.d("NavController", "Current destination: ${currentDestination?.id}")

        }
        lifecycleScope.launch {
            sharedViewModel.orderItems.collect { orderItems ->
                orderItemAdapter.submitList(orderItems)
                val totalPrice = sharedViewModel.totalPrice.value
                // viewModel.checkStudentLimit(totalPrice) // Закомментировал эту строку
            }
        }
    }
    private fun postOrdering(){
        lifecycleScope.launch{
            viewModel.postOrder(sharedViewModel.orderItems.value)
            viewModel.postOrderState.collect{ state ->
                when (state) {
                 is UiState.Loading ->{

                 }
                 is UiState.Success -> {
                     findNavController().navigate(R.id.action_cardFragmentAuthed_to_cardFragmentSuccess)
                 }
                 is UiState.Error -> {
                     findNavController().navigate(R.id.action_cardFragmentAuthed_to_cardFragmentError)
                     val errorMessage = state.message
                     Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                 }

                }
            }
        }
    }
    private fun updateDocument() {
        val date = "2024-11-21"

        viewModel.updateDocument(date)
        lifecycleScope.launchWhenStarted {
            viewModel.updateDocumentState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                    }
                    is UiState.Success -> {
                        val updatedDocument = state.data
                        Toast.makeText(requireContext(), "Документ обновлён", Toast.LENGTH_LONG).show()
                    }
                    is UiState.Error -> {
                        val errorMessage = state.message
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }




    /*private fun docs(){
        val cred = viewModel.credentials
        val cent = viewModel.canteenId
        val idRequest = DocumentResponse(
            id = 1,
            document_type = 1
        )
        CoroutineScope(Dispatchers.IO).launch(){
            try {
                val response = userApiservice.updateDocument(cred.toString(),cent,1,idRequest)

                withContext(Dispatchers.Main){
                    if (response.isSuccessful){
                        Toast.makeText(requireContext(), "Goyda", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_cardFragmentAuthed_to_cardFragmentInitial)
                    } else {
                        findNavController().navigate(R.id.action_cardFragmentAuthed_to_cardFragmentInitial)
                        Log.e("arsenchik", response.message())
                    }
                }
            } catch (e: Exception) {
                Log.e("arsenchik", "Error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "ne Goyda", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    */@SuppressLint("SetTextI18n")
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
                // Закомментированы проверки баланса и лимита
                // val balance = viewModel.student.value?.balance?.toDoubleOrNull() ?: 0.0
                // val limit = viewModel.studentLimit.value?.toDoubleOrNull() ?: Double.MAX_VALUE

                // val canPay = balance >= totalPrice
                // val withinLimit = totalPrice <= limit

                // if (!canPay || !withinLimit) {
                //     binding.tvTotal.setTextColor(requireContext().getColor(R.color.balance_error))
                //     binding.tvTotalLabel.setTextColor(requireContext().getColor(R.color.balance_error))
                //     binding.tvNotEnoughMoney.visibility = View.VISIBLE
                //     binding.mrlBtnPay.isEnabled = false
                //     binding.mrlBtnPay.isClickable = false
                //     binding.mrlBtnPay.setBackgroundColor(requireContext().getColor(R.color.disabled_btn))

                //     binding.tvNotEnoughMoney.text = when {
                //         !canPay -> "Недостаточно средств для оплаты"
                //         !withinLimit -> "Превышен лимит заказа"
                //         else -> "Недостаточно средств для дальнейшей оплаты"
                //     }
                // } else {
                //     binding.tvTotal.setTextColor(requireContext().getColor(android.R.color.black))
                //     binding.tvTotalLabel.setTextColor(requireContext().getColor(android.R.color.black))
                //     binding.tvNotEnoughMoney.visibility = View.INVISIBLE
                //     binding.mrlBtnPay.isEnabled = true
                //     binding.mrlBtnPay.isClickable = true
                //     binding.mrlBtnPay.setBackgroundColor(requireContext().getColor(R.color.primaryDark))
                // }

                // Убираем все проверки баланса и лимита
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
