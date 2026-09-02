package com.japanesehelper.di

import com.japanesehelper.data.remote.api.KanjiWordSetApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Networking for `POST /kanji-word-set`.
 *
 * Same backend/base URL/timeouts as the description and image-search clients
 * (see [DescriptionNetworkModule], [ImageSearchNetworkModule]), on its own
 * OkHttp/Retrofit instance following the same per-feature-module pattern.
 */
@Module
@InstallIn(SingletonComponent::class)
object KanjiWordSetNetworkModule {

    private const val BASE_URL = "http://89.167.34.196:8000/"

    // Matches the description/image-search clients: the backend calls Gemini,
    // so latency is variable.
    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val WRITE_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 60L
    private const val CALL_TIMEOUT_SECONDS = 90L

    @Provides
    @Singleton
    @Named("KanjiWordSetOkHttp")
    fun provideKanjiWordSetOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("KanjiWordSetRetrofit")
    fun provideKanjiWordSetRetrofit(
        @Named("KanjiWordSetOkHttp") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideKanjiWordSetApi(@Named("KanjiWordSetRetrofit") retrofit: Retrofit): KanjiWordSetApi {
        return retrofit.create(KanjiWordSetApi::class.java)
    }
}
