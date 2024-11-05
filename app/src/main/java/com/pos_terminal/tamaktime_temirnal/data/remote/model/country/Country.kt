package com.pos_terminal.tamaktime_temirnal.data.remote.model.country

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("name")
    var name: String? = null,
) : Parcelable {
    override fun toString(): String {
        return "Country(id=$id, name='$name')"
    }
}