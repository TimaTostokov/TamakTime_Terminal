package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.data.repositories.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _orderItems = MutableStateFlow<List<Product>>(emptyList())
    val orderItems: StateFlow<List<Product>> = _orderItems.asStateFlow()

    private val _products =
        MutableStateFlow<List<Product>>(savedStateHandle["products"] ?: emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    init {
        viewModelScope.launch {
            _products.collect { products ->
                savedStateHandle["products"] = products
            }
        }
    }

    fun loadProducts(newProducts: List<Product>) {
        _products.value = newProducts
    }

    private val _isUserAuthenticated = MutableStateFlow(false)
    val isUserAuthenticated: StateFlow<Boolean> = _isUserAuthenticated.asStateFlow()

    fun setUserAuthenticated(isAuthenticated: Boolean) {
        _isUserAuthenticated.value = isAuthenticated
    }

    fun resetOrder() {
        val updatedProducts = _products.value.map { originalProduct ->
            val orderItem = _orderItems.value.find { it.id == originalProduct.id }
            if (orderItem != null) {
                originalProduct.copy(
                    count = originalProduct.count + orderItem.cartCount,
                    cartCount = 0
                )
            } else {
                originalProduct.copy(cartCount = 0)
            }
        }

        _products.value = updatedProducts
        _orderItems.value = emptyList()
    }

    fun addProductToOrder(product: Product) {
        val currentProduct = _products.value.find { it.id == product.id }
        if (currentProduct == null || currentProduct.count <= 0) {
            return
        }
        val updatedProducts = _products.value.map {
            if (it.id == product.id && it.count > 0) {
                it.copy(count = it.count - 1, cartCount = it.cartCount + 1)
            } else it
        }
        _products.value = updatedProducts

        val existingItem = _orderItems.value.find { it.id == product.id }
        if (existingItem != null) {
            val updatedItem = existingItem.copy(cartCount = existingItem.cartCount + 1)
            _orderItems.value = _orderItems.value.map {
                if (it.id == updatedItem.id) updatedItem else it
            }
        } else {
            _orderItems.value += product.copy(cartCount = 1)
        }
    }

    fun removeProductFromOrder(product: Product) {
        val updatedProducts = _products.value.map {
            if (it.id == product.id) {
                it.copy(count = it.count + 1, cartCount = it.cartCount - 1)
            } else it
        }
        _products.value = updatedProducts

        val existingItem = _orderItems.value.find { it.id == product.id }
        if (existingItem != null && existingItem.cartCount > 1) {
            val updatedItem = existingItem.copy(cartCount = existingItem.cartCount - 1)
            _orderItems.value = _orderItems.value.map {
                if (it.id == updatedItem.id) updatedItem else it
            }
        } else {
            _orderItems.value = _orderItems.value.filter { it.id != product.id }
        }
    }

    fun deleteProductFromOrder(product: Product) {
        _orderItems.value = _orderItems.value.filter { it.id != product.id }
        val updatedProducts = _products.value.map {
            if (it.id == product.id) {
                it.copy(count = it.count + product.cartCount, cartCount = 0)
            } else it
        }
        _products.value = updatedProducts
    }

    val totalPrice: StateFlow<Double> = orderItems.map { items ->
        items.sumOf { (it.sellingPrice?.toDoubleOrNull() ?: 0.0) * it.cartCount }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

}