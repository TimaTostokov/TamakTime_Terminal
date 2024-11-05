package com.pos_terminal.tamaktime_temirnal.data.remote.model.student

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentCardKey(
    @SerializedName("key")
    val key: String? = null,
) : Parcelable