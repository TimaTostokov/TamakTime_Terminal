package com.pos_terminal.tamaktime_temirnal.data.remote.model.school

import com.google.gson.annotations.SerializedName
import com.pos_terminal.tamaktime_temirnal.data.remote.model.country.Address
import com.pos_terminal.tamaktime_temirnal.data.remote.model.language.Language
import com.pos_terminal.tamaktime_temirnal.data.remote.model.phone.Phone

data class School(
    @SerializedName("id")
    var id: Long,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("address")
    var address: Address? = null,
    @SerializedName("phones")
    var phones: List<Phone>,
    @SerializedName("language")
    var language: Language? = null,
) {

    override fun toString(): String {
        return "School(id=$id, name='$name', address=$address, phones=$phones, language=$language)"
    }

}