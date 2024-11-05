package com.pos_terminal.tamaktime_temirnal.data.remote.model.user

import com.google.gson.annotations.SerializedName
import com.pos_terminal.tamaktime_temirnal.data.remote.model.user.CanteenRole

data class User(
    @SerializedName("credentials")
    var credentials: String? = null,
    @SerializedName("id")
    val id: Long,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("photo")
    val photo: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("birth_date")
    val birthDate: String? = null,
    @SerializedName("canteen_roles")
    val canteens: List<CanteenRole>,
    @SerializedName("card_uuid")
    val cardUuid: String? = null
) {

    override fun toString(): String =
        "User(token=$credentials, id=$id, phone='$phone', firstName='$firstName'," +
                "\nlastName='$lastName', photo=$photo, email=$email, birthDate=$birthDate, " +
                "\ncanteens=$canteens, cardUuid=$cardUuid)"

}