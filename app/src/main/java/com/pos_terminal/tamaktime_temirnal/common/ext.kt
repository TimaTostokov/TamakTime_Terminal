package com.pos_terminal.tamaktime_temirnal.common

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

fun ImageView.loadImageApi(url: String) {
    val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
        url
    } else {
        "https://kantindeyim.com/$url"
    }
    Log.d("ImageLoading", "Loading image from URL: $fullUrl")
    Glide.with(this)
        .load(fullUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}