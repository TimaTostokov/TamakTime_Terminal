package com.pos_terminal.tamaktime_temirnal.data.remote.model.order

import com.pos_terminal.tamaktime_temirnal.data.remote.model.product.Product

data class OrderItemFull(
    var product: Product? = null,
    var count: Int = 1,
) {

    override fun toString(): String {
        return "OrderItemFull(product=$product, count=$count)"
    }
}