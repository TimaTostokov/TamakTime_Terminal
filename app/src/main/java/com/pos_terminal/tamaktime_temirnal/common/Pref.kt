package com.pos_terminal.tamaktime_temirnal.common

import android.content.Context

class Pref(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val FLAG_KEY = "flag_key"
    }

    fun setFlag(value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(FLAG_KEY, value)
            .apply()
    }

    fun getFlag(): Boolean {
        return sharedPreferences.getBoolean(FLAG_KEY, false)
    }

}