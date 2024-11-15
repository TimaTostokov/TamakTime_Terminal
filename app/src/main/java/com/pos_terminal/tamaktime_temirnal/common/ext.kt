package com.pos_terminal.tamaktime_temirnal.common

import android.widget.ImageView
import com.bumptech.glide.Glide


fun ImageView.loadImageURL(url: String?) {
    Glide.with(this).load(url).centerCrop().into(this)
}