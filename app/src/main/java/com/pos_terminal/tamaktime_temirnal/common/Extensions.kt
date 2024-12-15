@file:Suppress("DEPRECATION")

package com.pos_terminal.tamaktime_temirnal.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

object Extensions {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackbar(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(view, message, duration).show()
    }

    fun showSnackbarWithAction(
        view: View,
        message: String,
        actionText: String,
        action: () -> Unit,
        duration: Int = Snackbar.LENGTH_LONG
    ) {
        Snackbar.make(view, message, duration)
            .setAction(actionText) {
                action()
            }
            .show()
    }

    @SuppressLint("DefaultLocale")
    fun formatPrice(price: Double): String {
        if (price % 1.0 == 0.0) {
            return String.format("%.0f с", price)
        } else {
            return String.format("%.2f с", price)
        }
    }

    fun Activity.changeLanguage() {
        val listItems = arrayOf("English", "Русский", "Türkçe")
        val mBuilder = android.app.AlertDialog.Builder(this)
        mBuilder.setTitle("Выберите язык")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialog, which ->
            when (which) {
                0 -> setLocale("en", this)
                1 -> setLocale("ru", this)
                2 -> setLocale("tr", this)
            }
            dialog.dismiss()
            val intent = Intent(this, this::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    fun setLocale(languageCode: String, context: Context) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        LanguagePreference.getInstance(context).saveLanguage(languageCode)
    }

    fun loadLocale(context: Context) {
        val language = LanguagePreference.getInstance(context).getLanguage()
        if (!language.isNullOrEmpty()) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

}