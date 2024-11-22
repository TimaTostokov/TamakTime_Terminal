package com.pos_terminal.tamaktime_temirnal.presentation.activity

import androidx.lifecycle.ViewModel
import com.pos_terminal.tamaktime_temirnal.common.CardUUIDInteractor
import com.pos_terminal.tamaktime_temirnal.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cardUUIDInteractor: CardUUIDInteractor
) : ViewModel() {

    val hasCredentials: Flow<Boolean> = userRepository.flowHasCredentials()

    suspend fun exit() = userRepository.clear()

    fun setUuid(newUuid: String) {
        cardUUIDInteractor.setCardUuid(newUuid)
    }

}