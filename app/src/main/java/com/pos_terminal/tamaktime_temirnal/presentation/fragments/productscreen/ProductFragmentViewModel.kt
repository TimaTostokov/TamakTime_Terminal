package com.pos_terminal.tamaktime_temirnal.presentation.fragments.productscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos_terminal.tamaktime_temirnal.common.Resource
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.ProductsResponse
import com.pos_terminal.tamaktime_temirnal.data.repositories.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductFragmentViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _productsResponse = MutableStateFlow<ProductsResponse?>(null)
    val productsResponse: StateFlow<ProductsResponse?> = _productsResponse.asStateFlow()

    private val _products = MutableStateFlow<Set<Product>>(emptySet())
    val products: StateFlow<Set<Product>> = _products.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProducts(header: String, canteenId: Long, categoryId: Long) {
        viewModelScope.launch {
            _loading.value = true
            val result =
                productRepository.getProductsByCanteenIdAndCategoryId(header, canteenId, categoryId)
            _loading.value = false

            when (result.status) {
                Resource.Status.ERROR -> _error.value = result.message ?: "Unknown error"
                Resource.Status.SUCCESS -> result.data?.let { _productsResponse.value = it }
                Resource.Status.LOADING -> {
                    Timber.d("Card authentication is loading...")
                }
            }
        }
    }

}