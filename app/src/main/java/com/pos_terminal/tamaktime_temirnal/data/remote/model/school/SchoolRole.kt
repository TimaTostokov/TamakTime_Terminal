package com.pos_terminal.tamaktime_temirnal.data.remote.model.school

import com.google.gson.annotations.SerializedName

data class SchoolRole(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("school")
    var school: School? = null,
    @SerializedName("role")
    var role: String? = null,
) {

    override fun toString(): String {
        return "SchoolRole(id=$id, school=$school, role='$role')"
    }

}