package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.CardUUIDInteractor
import com.pos_terminal.tamaktime_temirnal.common.Resource
import com.pos_terminal.tamaktime_temirnal.common.UiState
import com.pos_terminal.tamaktime_temirnal.data.remote.model.documents.DocumentRequestBody
import com.pos_terminal.tamaktime_temirnal.data.remote.model.documents.DocumentResponse
import com.pos_terminal.tamaktime_temirnal.data.remote.model.documents.LineRequest
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderItem
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderResponse
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderToPost
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.data.remote.model.qr_order.QROrderItem
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.Student
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.StudentCardKey
import com.pos_terminal.tamaktime_temirnal.data.repositories.documents.DocumentRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.order.OrderRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.StudentRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.limit.StudentLimitRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.qr.QrOrderRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CardFragmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val cardUUIDInteractor: CardUUIDInteractor,
    private val docsRepository: DocumentRepository,
    private val studentLimitRepository: StudentLimitRepository,
    private val orderRepository: OrderRepository,
    private val qrOrderRepository: QrOrderRepository,
) : ViewModel() {

    private var orderingSuccess: Boolean? = null
    private var orderSuccessChange = false

    private val _postOrderState = MutableStateFlow<UiState<OrderResponse>>(UiState.Loading)
    val postOrderState: StateFlow<UiState<OrderResponse>> = _postOrderState.asStateFlow()

    private val _orderingState = MutableStateFlow<UiState<OrderResponse>>(UiState.Loading)
    val orderingState: StateFlow<UiState<OrderResponse>> = _orderingState.asStateFlow()

    private val _updateDocumentState = MutableStateFlow<UiState<DocumentResponse>>(UiState.Loading)
    val updateDocumentState: StateFlow<UiState<DocumentResponse>> = _updateDocumentState.asStateFlow()

    val credentials: Flow<String?> = userRepository.flowCredentials()

    val canteenId: StateFlow<Long?> = userRepository.flowCanteenId() as StateFlow<Long?>

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

    private val _key1 = MutableStateFlow("")
    val key1: StateFlow<String> = _key1.asStateFlow()

    private val _key2 = MutableStateFlow("")
    val key2: StateFlow<String> = _key2.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private var orderId = -1L

    private val _studentLimit = MutableStateFlow<String?>(null)
    val studentLimit: StateFlow<String?> = _studentLimit.asStateFlow()

    fun setCardUuid(uuid: String) {
        _cardUuid.value = uuid
    }

    fun authenticateCard() {
        _cardState.value = CardState.AUTHENTICATING
        _loading.value = true

        viewModelScope.launch {

            val credentials = userRepository.getCredentials() ?: return@launch
            val schoolId = userRepository.getSchoolId() ?: return@launch

//            val cardUUID = _cardUuid.value ?: return@launch
//            cardUUIDInteractor.cardUuid.takeIf { it.isNotEmpty() }
//                ?: _cardUuid.value ?: return@launch

            val cardUUID = "62A2742E"

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
    fun updateDocument(date: String) {
        _updateDocumentState.value = UiState.Loading

        viewModelScope.launch {
            _updateDocumentState.value = UiState.Loading
            try {
                val authHeader = userRepository.getCredentials() ?: return@launch
                val canteenId = userRepository.getCanteenId().toString() ?: return@launch

                val documentRequestBody = DocumentRequestBody(
                    date = date,
                    docsType = 1,
                )


                val result = docsRepository.updateDocument(
                    authHeader = authHeader,
                    canteenId = canteenId,
                    documentId = "1L",
                    documentRequestBody = documentRequestBody
                )

                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        val data = result.data ?: throw Exception("Пустой ответ от сервера")
                        Log.d("arsenchik228","${result.data}")
                        Log.d("arsenchik228","${result}")
                        _updateDocumentState.value = UiState.Success(data)
                    }

                    Resource.Status.ERROR -> {
                        _updateDocumentState.value = UiState.Error(
                            throwable = Exception(result.message ?: "Неизвестная ошибка"),
                            message = result.message ?: "Произошла ошибка"
                        )
                    }

                    Resource.Status.LOADING -> {
                        _updateDocumentState.value = UiState.Loading
                    }
                }
            } catch (e: Exception) {
                _updateDocumentState.value = UiState.Error(
                    throwable = e,
                    message = e.localizedMessage ?: "Неизвестная ошибка"
                )
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

    fun resetCardState() {
        _cardState.value = CardState.INITIAL
        _key1.value = ""
        _key2.value = ""
        _cardUuid.value = ""
        _errorMessage.value = ""
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
                _studentLimit.emit(null)
            }
        }
    }

    fun checkStudentLimit(totalPrice: Double) {
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
                Resource.Status.LOADING -> {}

                Resource.Status.ERROR -> {
                    _cardState.value = CardState.ORDER_ERROR
                    Log.e("checkStudentLimit", "Error: ${studentLimit.message}")
                }

                Resource.Status.SUCCESS -> {
                    Log.d("checkStudentLimit", "Limit: ${studentLimit.data?.limit}")

                    if (studentLimit.data?.limit != null && totalPrice >= studentLimit.data.limit.toDouble()) {
                        _cardState.value = CardState.ORDER_ERROR
                    } else {
                        _cardState.value = CardState.ORDER
                    }
                }
            }
        }
    }

    fun postOrder(orderItems: List<Product>) {
        _cardState.value = CardState.ORDERING
        val orderList = mutableListOf<OrderItem>()
        if (orderItems.isNotEmpty()) {
            orderItems.forEach { product ->
                orderList.add(OrderItem(product.id, product.cartCount))
            }

            viewModelScope.launch {
                val credentials = userRepository.getCredentials() ?: return@launch
                val canteenId = userRepository.getCanteenId() ?: return@launch
                val orderToPost = OrderToPost(orderList)
                if (canteenId > 0) {
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

    fun mockupOrdering() = viewModelScope.launch {
        _cardState.value = CardState.ORDERING
        orderingSuccess = orderingSuccess ?: Random.nextBoolean()

        if (orderingSuccess == true) {
            _cardState.value = CardState.ORDER_SUCCESS
        } else {
            _cardState.value = CardState.ORDER_ERROR
            orderingSuccess = true
            orderSuccessChange = true
        }
    }

    interface CardNavigationListener {
        fun navigateToCategories()
    }

}