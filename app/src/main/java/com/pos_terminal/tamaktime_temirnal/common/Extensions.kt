package com.pos_terminal.tamaktime_temirnal.common

import android.content.Context
import android.content.res.Configuration
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import java.util.Locale

object Extensions {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun ImageView.loadImage(url: String) {
        Glide.with(this).load(url).into(this)
    }

    @Suppress("DEPRECATION")
    private fun setLocale(s: String, context: Context) {
        val locale = Locale(s)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
        LanguagePreference.getInstance(context)?.saveLanguage(s)
    }

    fun loadLocale(context: Context) {
        val language: String? = LanguagePreference.getInstance(context)?.getLanguage
        if (language != null) {
            setLocale(language, context)
        }
    }

}