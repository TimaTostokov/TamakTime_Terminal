package com.pos_terminal.tamaktime_temirnal.data.remote.model.documents

import com.google.gson.annotations.SerializedName

data class DocumentRequestBody(
    val date: String,
    @SerializedName("document_type")
    val docsType: Int = 1,
    val lines: List<LineRequest>
)

data class LineRequest(
    @SerializedName("product_id")
    val productId: Long,
    val quantity: String,
    val price: String?
)