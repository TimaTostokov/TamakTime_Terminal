package com.pos_terminal.tamaktime_temirnal.common

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide

object Extensions {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun ImageView.loadImage(url: String) {
        Glide.with(this).load(url).into(this)
    }

    @SuppressLint("DefaultLocale")
    fun formatPrice(price: Double): String {
        if (price % 1.0 == 0.0) {
            return String.format("%.0f с", price)
        } else {
            return String.format("%.2f с", price)
        }
    }

}