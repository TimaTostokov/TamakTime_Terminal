package com.pos_terminal.tamaktime_temirnal.data.remote.model.user

import com.google.gson.annotations.SerializedName
import com.pos_terminal.tamaktime_temirnal.data.remote.model.user.Canteen

data class CanteenRole(
    @SerializedName("canteen")
    val canteen: Canteen? = null,
    @SerializedName("role")
    val role: String? = null,
) {

    override fun toString(): String {
        return "CanteenRole(canteen=$canteen, role='$role')"
    }

}