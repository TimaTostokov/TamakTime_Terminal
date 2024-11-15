package com.pos_terminal.tamaktime_temirnal.presentation.fragments.productscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos_terminal.tamaktime_temirnal.common.Resource
import com.pos_terminal.tamaktime_temirnal.common.UiState
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.ProductsResponse
import com.pos_terminal.tamaktime_temirnal.data.repositories.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ProductsResponse>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProductsResponse>> = _uiState.asStateFlow()

    fun loadProducts(header: String, canteenId: Long,categoryId: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = productRepository.getProductsByCanteenIdAndCategoryId(header = header,
                canteenId = canteenId,
                categoryId = categoryId)
            _uiState.value = when (result.status) {
                Resource.Status.SUCCESS -> {
                    result.data?.let { UiState.Success(it) } ?: UiState.Error(
                        Exception("Empty data"),
                        "No products found"
                    )
                }
                Resource.Status.ERROR -> UiState.Error(
                    Exception(result.message ?: "Unknown error"),
                    result.message ?: "Unknown error"
                )
                Resource.Status.LOADING -> UiState.Loading
            }
        }
    }
}
