package com.pos_terminal.tamaktime_temirnal.data.remote.model.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderResponseItem(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("product_id")
    val productId: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("price")
    val price: String? = null,
    @SerializedName("quantity")
    val quantity: Int? = null,
) : Parcelable {

    override fun toString(): String {
        return "OrderResponseItem(id=$id, productId=$productId, title='$title', description='$description', price='$price', quantity=$quantity)"
    }

}