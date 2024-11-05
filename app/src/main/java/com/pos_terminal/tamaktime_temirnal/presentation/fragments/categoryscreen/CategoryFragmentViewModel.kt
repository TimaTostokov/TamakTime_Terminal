package com.pos_terminal.tamaktime_temirnal.presentation.fragments.categoryscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos_terminal.tamaktime_temirnal.common.Resource
import com.pos_terminal.tamaktime_temirnal.data.remote.model.category.Category
import com.pos_terminal.tamaktime_temirnal.data.repositories.category.CategoryRepository
import com.pos_terminal.tamaktime_temirnal.data.repositories.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryFragmentViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _catList = MutableStateFlow<List<Category>>(emptyList())
    val catList: StateFlow<List<Category>> = _catList.asStateFlow()

    val credentials: Flow<String?> = userRepository.flowCredentials()

    val canteenId: StateFlow<Long?> = userRepository.flowCanteenId()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    suspend fun logOut() = userRepository.logOut()

    fun getAllCategories(header: String, canteenId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            setLoadingState(true)
            _loading.value = true
            val result = categoryRepository.getCategories(header, canteenId)
            when (result.status) {
                Resource.Status.LOADING -> {
                    _loading.value = true
                    Timber.d("Card authentication is loading...")
                }

                Resource.Status.SUCCESS -> {
                    _loading.value = false
                    result.data?.let {
                        _catList.value = it
                    }
                    setLoadingState(false)
                }

                Resource.Status.ERROR -> {
                    _loading.value = false
                    result.message?.let {
                        _error.value = it
                    }
                    setLoadingState(false)
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _loading.value = isLoading
    }

}