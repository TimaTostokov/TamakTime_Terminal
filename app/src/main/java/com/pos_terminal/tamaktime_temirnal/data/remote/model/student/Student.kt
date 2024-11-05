package com.pos_terminal.tamaktime_temirnal.data.remote.model.student

import com.google.gson.annotations.SerializedName
import com.pos_terminal.tamaktime_temirnal.data.remote.model.school.School

data class Student(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("first_name")
    var firstName: String? = null,
    @SerializedName("last_name")
    var lastName: String? = null,
    @SerializedName("birth_date")
    var birthDate: String? = null,
    @SerializedName("phone")
    var phone: String? = null,
    @SerializedName("card_uuid")
    var cardUuid: String? = null,
    @SerializedName("active")
    var isActive: Boolean = false,
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("balance")
    var balance: String? = null,
    @SerializedName("group")
    var group: String? = null,
    @SerializedName("school")
    var school: School? = null,
    @SerializedName("photo")
    var photo: String? = null,
)