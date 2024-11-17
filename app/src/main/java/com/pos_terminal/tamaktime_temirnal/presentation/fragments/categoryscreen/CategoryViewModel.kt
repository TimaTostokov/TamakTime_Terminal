package com.pos_terminal.tamaktime_temirnal.presentation.fragments.categoryscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos_terminal.tamaktime_temirnal.common.Resource
import com.pos_terminal.tamaktime_temirnal.common.UiState
import com.pos_terminal.tamaktime_temirnal.data.remote.model.category.Category
import com.pos_terminal.tamaktime_temirnal.data.repositories.category.CategoryRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Category>>> = _uiState

    val credentials: Flow<String?> = userRepository.flowCredentials()
    
    val canteenId: StateFlow<Long?> = userRepository.flowCanteenId() as StateFlow<Long?>

    suspend fun logOut() = userRepository.logOut()

    fun getAllCategories(header: String, canteenId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            val result = categoryRepository.getCategories(header, canteenId)

            when (result.status) {
                Resource.Status.LOADING -> {
                    _uiState.value = UiState.Loading
                }

                Resource.Status.SUCCESS -> {
                    result.data?.let {
                        _uiState.value = UiState.Success(it)
                    } ?: run {
                        _uiState.value = UiState.Error(
                            Throwable("No categories found"),
                            "No categories available."
                        )
                    }
                }

                Resource.Status.ERROR -> {
                    result.message?.let { message ->
                        _uiState.value = UiState.Error(Throwable(message), message)
                    } ?: run {
                        _uiState.value =
                            UiState.Error(Throwable("Unknown error"), "Unknown error occurred.")
                    }
                }
            }
        }
    }

}