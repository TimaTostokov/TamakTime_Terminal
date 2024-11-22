package com.pos_terminal.tamaktime_temirnal.common

import android.content.Context
import android.content.SharedPreferences

class LanguagePreference private constructor(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("my_language", Context.MODE_PRIVATE)

    fun getLanguage(): String? {
        return preferences.getString("language_", "en")
    }

    fun saveLanguage(languageCode: String) {
        preferences.edit().putString("language_", languageCode).apply()
    }

    companion object {
        @Volatile
        private var instance: LanguagePreference? = null

        fun getInstance(context: Context): LanguagePreference {
            return instance ?: synchronized(this) {
                instance ?: LanguagePreference(context).also { instance = it }
            }
        }
    }

}