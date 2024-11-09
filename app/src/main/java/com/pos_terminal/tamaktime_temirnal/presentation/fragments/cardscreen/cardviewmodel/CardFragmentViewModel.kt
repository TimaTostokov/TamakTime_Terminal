package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardviewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos_terminal.tamaktime_temirnal.common.CardState
import com.pos_terminal.tamaktime_temirnal.common.CardUUIDInteractor
import com.pos_terminal.tamaktime_temirnal.common.Resource
import com.pos_terminal.tamaktime_temirnal.data.remote.model.qr_order.QROrderItem
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.Student
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.StudentRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.student.qr.QrOrderRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardFragmentViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val cardUUIDInteractor: CardUUIDInteractor,
    private val qrOrderRepository: QrOrderRepository
) : ViewModel() {

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

}