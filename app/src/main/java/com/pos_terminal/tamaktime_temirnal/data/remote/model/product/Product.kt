package com.pos_terminal.tamaktime_temirnal.data.remote.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("thumbnail")
    var thumbnail: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("buying_price")
    val buyingPrice: String? = null,
    @SerializedName("selling_price")
    val sellingPrice: String? = null,
    @SerializedName("available")
    val available: Boolean = false,
    @SerializedName("count")
    var count: Int = 0,
    var cartCount: Int = 0
) : Parcelable