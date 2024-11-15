package com.pos_terminal.tamaktime_temirnal.data.remote.model.order

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("items")
    val items: List<OrderResponseItem>,
    @SerializedName("total")
    val total: String? = null,
    @SerializedName("creator_id")
    val creatorId: Int? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("transaction_id")
    val transactionId: String? = null,
) {
    override fun toString(): String {
        return "OrderResponse(id=$id, status=$status, items=$items, total='$total', creatorId=$creatorId, createdAt='$createdAt', updatedAt='$updatedAt', transactionId='$transactionId')"
    }

}