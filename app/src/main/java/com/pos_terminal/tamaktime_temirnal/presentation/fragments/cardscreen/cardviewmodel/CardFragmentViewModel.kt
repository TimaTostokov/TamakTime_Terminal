package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.CardUUIDInteractor
import com.pos_terminal.tamaktime_temirnal.common.Resource
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderItem
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderItemFull
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderToPost
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.data.remote.model.qr_order.QROrderItem
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.Student
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.StudentCardKey
import com.pos_terminal.tamaktime_temirnal.data.repositories.order.OrderRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.StudentRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.limit.StudentLimitRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.qr.QrOrderRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CardFragmentViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val cardUUIDInteractor: CardUUIDInteractor,
    private val studentLimitRepository: StudentLimitRepository,
    private val qrOrderRepository: QrOrderRepository
) : ViewModel() {

    val gson = Gson()

    val orderMap = MutableStateFlow<MutableMap<Long, OrderItemFull>>(mutableMapOf())
    private var orderId = -1L

    private var orderingSuccess: Boolean? = null
    private var orderSuccessChange = false

    private val _student = MutableStateFlow<Student?>(null)
    val student: StateFlow<Student?> = _student.asStateFlow()

    private val _studentQR = MutableStateFlow<QROrderItem?>(null)
    val studentQR: StateFlow<QROrderItem?> = _studentQR.asStateFlow()

    private val _cardState = MutableStateFlow(CardState.INITIAL)
    val cardState: StateFlow<CardState> = _cardState.asStateFlow()

    private val _cardUuid = MutableStateFlow<String?>(null)
    val cardUuid: StateFlow<String?> = _cardUuid.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    val _totalPrice = MutableStateFlow(0.0)

    private val _orderList = MutableStateFlow<List<OrderItemFull>>(emptyList())
    val orderList: StateFlow<List<OrderItemFull>> = _orderList.asStateFlow()

    private val _key1 = MutableStateFlow("")
    val key1: StateFlow<String> = _key1.asStateFlow()

    private val _key2 = MutableStateFlow("")
    val key2: StateFlow<String> = _key2.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _studentLimit = MutableStateFlow<String?>(null)
    val studentLimit: StateFlow<String?> = _studentLimit.asStateFlow()

    fun setCardUuid(uuid: String) {
        _cardUuid.value = uuid
    }

    private fun updateTotalPrice(addedPrice: Double) {
        _totalPrice.value += addedPrice
    }

    private fun updateList() {
        _orderList.value = orderMap.value.values.toList()
    }

    fun authenticateCard() {
        _cardState.value = CardState.AUTHENTICATING
        _loading.value = true

        viewModelScope.launch {

            val credentials = userRepository.getCredentials() ?: return@launch
            val schoolId = userRepository.getSchoolId() ?: return@launch
            val cardUUID = _cardUuid.value ?: return@launch
            cardUUIDInteractor.cardUuid.takeIf { it.isNotEmpty() }
                ?: _cardUuid.value ?: return@launch

            if (schoolId > 0) {

                val result = studentRepository.getStudentBySchoolIdAndCardUUID(
                    credentials, schoolId, cardUUID
                )

                when (result.status) {
                    Resource.Status.LOADING -> {
                        _loading.value = true
                    }

                    Resource.Status.ERROR -> {
                        _loading.value = false
                        _cardState.value = CardState.AUTHENTICATING_ERROR
                        _errorMessage.value = result.message ?: "Произошла ошибка"
                    }

                    Resource.Status.SUCCESS -> {
                        _loading.value = false
                        _cardState.value = CardState.AUTHENTICATED
                        _student.value = result.data ?: run {
                            _cardState.value = CardState.AUTHENTICATING_ERROR
                            null
                        }
                    }
                }
            }
        }
    }

    fun authenticateStudentByQR(cardUUID: String) {
        _cardState.value = CardState.AUTHENTICATING
        _loading.value = true

        viewModelScope.launch {
            val credentials = userRepository.getCredentials() ?: return@launch
            val result = qrOrderRepository.getStudentByQR(credentials, cardUUID)

            when (result.status) {
                Resource.Status.LOADING -> {
                    _loading.value = true
                }

                Resource.Status.ERROR -> {
                    _cardState.value = CardState.AUTHENTICATING_ERROR
                    Timber.e(result.message)
                    Log.e("marsel", "NatureError: ${result.message.toString()}")
                }

                Resource.Status.SUCCESS -> {
                    _loading.value = false
                    Log.e("marsel", "Success: ${result.message.toString()}")

                    val qrOrderItems = result.data
                    if (!qrOrderItems.isNullOrEmpty()) {
                        val studentData = qrOrderItems[0].creator
                        _student.value = studentData
                        if (_student.value != null) {
                            _cardState.value = CardState.AUTHENTICATED
                        } else {
                            _cardState.value = CardState.AUTHENTICATING_ERROR
                            Log.e("authenticateStudentByQR", "Received student data is null")
                        }
                    } else {
                        _cardState.value = CardState.AUTHENTICATING_ERROR
                        Log.e("authenticateStudentByQR", "No items received")
                    }
                }
            }
        }
    }

    interface CardNavigationListener {
        fun navigateToCategories()
    }

    fun addToCart(product: Product) {
        val newMap = orderMap.value.toMutableMap()

        newMap[product.id]?.let { existingOrderItem ->
            val updatedItem = existingOrderItem.copy(count = existingOrderItem.count + 1)
            newMap[product.id] = updatedItem
            Log.d("Product updated:", " $updatedItem")
        } ?: run {
            newMap[product.id] = OrderItemFull(product).apply { count = 1 }
            Log.d("Product added:", " ${newMap[product.id]}")
        }

        orderMap.value = newMap
        _cardState.value = CardState.ORDER
        updateTotalPrice()
        checkStudentLimit()
    }

    fun itemIncrement(orderItemFull: OrderItemFull) {
        val newMap = orderMap.value.toMutableMap()
        newMap[orderItemFull.product?.id]?.let { orderItem ->
            orderItem.count += 1
        }
        orderMap.value = newMap
        updateTotalPrice()
    }

    fun itemDecrement(orderItemFull: OrderItemFull) {
        val newMap = orderMap.value.toMutableMap()
        newMap[orderItemFull.product?.id]?.let { orderItem ->
            if (orderItem.count > 1) {
                orderItem.count -= 1
            } else {
                newMap.remove(orderItemFull.product?.id)
            }
        }
        orderMap.value = newMap
        updateTotalPrice()
    }

    fun updateTotalPrice() {
        var totalPrice = 0.0
        orderMap.value.forEach { (_, orderItem) ->
            val itemPrice = orderItem.product?.sellingPrice?.toDouble() ?: 0.0
            totalPrice += itemPrice * orderItem.count
        }
        _totalPrice.value = totalPrice
    }

    fun itemDelete(orderItemFull: OrderItemFull) {
        val newMap = orderMap.value.toMutableMap()
        orderItemFull.product?.let { newMap.remove(it.id) }
        orderMap.value = newMap
    }

    fun resetCardState() {
        _cardState.value = CardState.INITIAL
        _key1.value = ""
        _key2.value = ""
        _cardUuid.value = ""
        _totalPrice.value = 0.0
        _errorMessage.value = ""
        orderMap.value.clear()
        orderId = -1L
        orderingSuccess = null
    }

    fun loadStudentLimit(studentId: Long) {
        viewModelScope.launch {
            val credentials = userRepository.getCredentials() ?: return@launch
            val result = studentLimitRepository.getStudentLimit(credentials, studentId)
            if (result.status == Resource.Status.SUCCESS) {
                _studentLimit.emit(result.data?.limit)
            } else {
                Log.e("arsen_botik228", "Error loading student limit: ${result.message}")
                _studentLimit.emit(null)
            }
        }
    }

    private fun checkStudentLimit() {
        viewModelScope.launch {
            val credentials = userRepository.getCredentials() ?: run {
                Log.e("checkStudentLimit", "Credentials are null")
                return@launch
            }

            val studentId = userRepository.getStudentId() ?: run {
                Log.e("checkStudentLimit", "Student ID is null")
                return@launch
            }

            val studentLimit = studentLimitRepository.getStudentLimit(credentials, studentId)

            when (studentLimit.status) {
                Resource.Status.LOADING -> {
                }

                Resource.Status.ERROR -> {
                    _cardState.value = CardState.ORDER_ERROR
                    orderingSuccess = false
                    Log.e("checkStudentLimit", "Error: ${studentLimit.message}")
                }

                Resource.Status.SUCCESS -> {
                    val totalPrice = _totalPrice.value
                    Log.d("checkStudentLimit", "Limit: ${studentLimit.data?.limit}")

                    if (studentLimit.data?.limit != null && totalPrice >= studentLimit.data.limit.toDouble()) {
                        _cardState.value = CardState.ORDER_ERROR
                        orderingSuccess = false
                    } else {
                        _cardState.value = CardState.ORDER
                        orderingSuccess = true
                    }
                }
            }
        }
    }

    fun postOrder() {
        _cardState.value = CardState.ORDERING
        val orderList = mutableListOf<OrderItem>()
        if (orderMap.value.values.isNotEmpty()) {
            orderMap.value.values.forEach {
                it.product?.let { product ->
                    orderList.add(OrderItem(product.id, it.count))
                }
            }

            viewModelScope.launch {
                val credentials = userRepository.getCredentials() ?: return@launch
                val canteenId = userRepository.getCanteenId() ?: return@launch
                val orderToPost = OrderToPost(orderList)
                if (canteenId > 0 && credentials != null) {
                    val result = orderRepository.postOrder(credentials, canteenId, orderToPost)
                    when (result.status) {
                        Resource.Status.LOADING -> {
                        }

                        Resource.Status.ERROR -> {
                            _cardState.value = CardState.ORDER_ERROR
                        }

                        Resource.Status.SUCCESS -> {
                            _cardState.value = CardState.ORDER_SUCCESS
                        }
                    }
                }
            }
        }
    }

    fun ordering() {
        _cardState.value = CardState.ORDERING
        viewModelScope.launch {
            val credentials = userRepository.getCredentials() ?: return@launch
            val canteenId = userRepository.getCanteenId() ?: return@launch
            val cardKey = "${_key1.value}${_key2.value}".replace("-", "")

            val result = orderRepository.ordering(
                credentials,
                canteenId,
                orderId,
                StudentCardKey(cardKey)
            )

            when (result.status) {
                Resource.Status.LOADING -> {
                }

                Resource.Status.ERROR -> {
                    _cardState.value = CardState.ORDER_ERROR
                }

                Resource.Status.SUCCESS -> {
                    _cardState.value = CardState.ORDER_SUCCESS
                }
            }
        }
    }

}