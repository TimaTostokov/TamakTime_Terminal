package com.pos_terminal.tamaktime_temirnal.presentation.fragments.cardscreen.cardauthed

import androidx.lifecycle.ViewModel
import com.pos_terminal.tamaktime_temirnal.common.UiState
import com.pos_terminal.tamaktime_temirnal.data.remote.model.order.OrderItemFull
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product
import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.ProductsResponse
import com.pos_terminal.tamaktime_temirnal.data.repositories.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _orderItems = MutableStateFlow<List<OrderItemFull>>(emptyList())
    val orderItems: StateFlow<List<OrderItemFull>> = _orderItems.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    fun addProductToOrder(product: Product) {
        val updatedProducts = _products.value.map {
            if (it.id == product.id && it.count!! > 0) {
                it.copy(count = it.count!! - 1)
            } else {
                it
            }
        }
        _products.value = updatedProducts
        val existingItem = _orderItems.value.find { it.product?.id == product.id }
        if (existingItem != null) {
            val updatedItem = existingItem.copy(count = existingItem.count + 1)
            _orderItems.value = _orderItems.value.map {
                if (it.product?.id == updatedItem.product?.id) updatedItem else it
            }
        } else {
            val newItem = OrderItemFull(product, count = 1)
            _orderItems.value = _orderItems.value + newItem
        }
    }

    fun removeProductFromOrder(orderItem: OrderItemFull) {
        val updatedProducts = _products.value.map {
            if (it.id == orderItem.product?.id) it.copy(count = it.count!! + 1) else it
        }
        _products.value = updatedProducts

        val existingItem = _orderItems.value.find { it.product?.id == orderItem.product?.id }
        if (existingItem != null && existingItem.count > 1) {
            val updatedItem = existingItem.copy(count = existingItem.count - 1)
            _orderItems.value = _orderItems.value.map {
                if (it.product?.id == updatedItem.product?.id) updatedItem else it
            }
        } else {
            _orderItems.value = _orderItems.value.filter { it.product?.id != orderItem.product?.id }
        }
    }
}

