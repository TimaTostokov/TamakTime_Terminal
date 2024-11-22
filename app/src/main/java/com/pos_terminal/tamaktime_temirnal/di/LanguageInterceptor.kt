package com.pos_terminal.tamaktime_temirnal.di

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

        val newRequest = originalRequest.newBuilder()
            .addHeader("Accept-Language", languageCode)
            .build()

        return chain.proceed(newRequest)
    }

}