package com.japanesehelper.di

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Shared setup for the Gemini-backed endpoints. Backend latency is variable -
 * observed from ~12s to beyond 30s - so read/call timeouts carry a wide margin.
 */
internal object BackendHttp {

    const val BASE_URL = "http://89.167.34.196:8000/"

    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val WRITE_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 60L
    private const val CALL_TIMEOUT_SECONDS = 90L

    fun clientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)

    fun retrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}
