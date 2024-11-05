package com.pos_terminal.tamaktime_temirnal.data.remote.model.phone

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Phone(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("number")
    var number: String? = null,
) : Parcelable {

    override fun toString(): String {
        return "Phone(id=$id, number='$number')"
    }

}