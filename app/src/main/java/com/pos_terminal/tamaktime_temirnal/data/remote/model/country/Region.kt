package com.pos_terminal.tamaktime_temirnal.data.remote.model.country

import com.google.gson.annotations.SerializedName

data class Region(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("county")
    var country: Country? = null,
) {

    override fun toString(): String {
        return "Region(id=$id, name='$name', country=$country)"
    }

}