package com.pos_terminal.tamaktime_temirnal.di

import android.util.Log
import com.pos_terminal.tamaktime_temirnal.common.LanguagePreference
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject

class LanguageInterceptor @Inject constructor(
    private val languagePreference: LanguagePreference,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val languageCode = languagePreference.getLanguage() ?: Locale.getDefault().language
        Log.e("ololo", "работает${languageCode}")
        val newRequest = originalRequest.newBuilder()
            .addHeader("Content-Language", languageCode)
            .build()

        return chain.proceed(newRequest)
    }

}