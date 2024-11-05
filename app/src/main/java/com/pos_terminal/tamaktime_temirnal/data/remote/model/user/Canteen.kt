package com.pos_terminal.tamaktime_temirnal.data.remote.model.user

import com.google.gson.annotations.SerializedName
import com.pos_terminal.tamaktime_temirnal.data.remote.model.school.School

data class Canteen(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("school")
    val school: School? = null,
) {

    override fun toString(): String {
        return "Canteen(id=$id, name='$name', avatar='$avatar, school=$school')"
    }

}