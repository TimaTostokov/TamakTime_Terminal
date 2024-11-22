package com.pos_terminal.tamaktime_temirnal.data.remote.model.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderToPost(
    @SerializedName("products")
    val products: List<OrderItem>,
    @SerializedName("total") val total: Double
) : Parcelable