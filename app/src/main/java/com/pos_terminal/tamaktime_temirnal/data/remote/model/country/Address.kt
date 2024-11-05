package com.pos_terminal.tamaktime_temirnal.data.remote.model.country

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("district")
    var district: District? = null,
    @SerializedName("street")
    var street: String? = null,
    @SerializedName("number")
    var number: String? = null,
) {

    override fun toString(): String {
        return "Address(id=$id, district=$district, street='$street', number='$number')"
    }

}