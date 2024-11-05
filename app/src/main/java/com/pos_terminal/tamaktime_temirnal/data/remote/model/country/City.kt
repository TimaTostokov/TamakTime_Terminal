package com.pos_terminal.tamaktime_temirnal.data.remote.model.country

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("region")
    var region: Region? = null,
) {
    override fun toString(): String {
        return "City(id=$id, name='$name', region=$region)"
    }

}