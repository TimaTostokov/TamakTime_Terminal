package com.pos_terminal.tamaktime_temirnal.data.remote.model.qr_order

import com.google.gson.annotations.SerializedName
import com.pos_terminal.tamaktime_temirnal.data.remote.model.student.Student

data class QROrderItem(
    @SerializedName("cart_items")
    val cartItems: List<CartItem>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("creator")
    val creator: Student,
    @SerializedName("id")
    val id: Int,
    @SerializedName("total")
    val total: String,
    @SerializedName("updated_at")
    val updatedAt: String
)