package com.pos_terminal.tamaktime_temirnal.data.remote.model.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("color")
    val color: String? = null,
) : Parcelable {

    override fun toString(): String {
        return "Category(id=$id, name='$name', icon=$icon, color=$color)"
    }

}