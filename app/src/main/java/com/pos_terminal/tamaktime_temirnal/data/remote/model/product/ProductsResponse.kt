package com.pos_terminal.tamaktime_temirnal.data.remote.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductsResponse(
    @SerializedName("items_count")
    val itemsCount: Int? = null,
    @SerializedName("pages_count")
    val pagesCount: Int? = null,
    @SerializedName("next")
    val next: String? = null,
    @SerializedName("previous")
    val previous: String? = null,
    @SerializedName("results")
    val results: List<Product>,
) : Parcelable