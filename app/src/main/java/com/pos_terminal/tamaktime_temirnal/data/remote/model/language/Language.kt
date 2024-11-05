package com.pos_terminal.tamaktime_temirnal.data.remote.model.language

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Language(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("name")
    var name: String? = null,
) : Parcelable {

    override fun toString(): String {
        return "Language(id=$id, name='$name')"
    }

}