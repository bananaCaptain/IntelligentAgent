package com.plantain.intelligentagent.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object NetworkClient {

    private const val TAG = "HttpLogger"

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        if (message.contains("Authorization:", ignoreCase = true)) {
            android.util.Log.d(TAG, message.replace(Regex("Authorization:\\s*Bearer\\s+[^\\s]+"), "Authorization: Bearer ***"))
        } else {
            android.util.Log.d(TAG, message)
        }
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}
