package com.pos_terminal.tamaktime_temirnal.data.remote.model.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    @SerializedName("product_id")
    var id: Long,
    @SerializedName("quantity")
    var count: Int? = null,
) : Parcelable {
    override fun toString(): String {
        return "OrderItem(id=$id, count=$count)"
    }

}