package com.pos_terminal.tamaktime_temirnal.data.remote.model.country

import com.google.gson.annotations.SerializedName

data class District(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("city")
    var city: City? = null,
) {

    override fun toString(): String {
        return "District(id=$id, name='$name', city=$city)"
    }

}