package com.japanesehelper.di

import com.japanesehelper.data.remote.api.TemperatureDescriptionApi
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
 * Networking for `POST /temperature-description`.
 *
 * Same backend/base URL/timeouts as the description/kanji-word-set clients
 * (see [DescriptionNetworkModule], [KanjiWordSetNetworkModule]), on its own
 * OkHttp/Retrofit instance following the same per-feature-module pattern.
 */
@Module
@InstallIn(SingletonComponent::class)
object TemperatureDescriptionNetworkModule {

    private const val BASE_URL = "http://89.167.34.196:8000/"

    // Matches the description/kanji-word-set clients: the backend calls
    // Gemini, so latency is variable.
    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val WRITE_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 60L
    private const val CALL_TIMEOUT_SECONDS = 90L

    @Provides
    @Singleton
    @Named("TemperatureDescriptionOkHttp")
    fun provideTemperatureDescriptionOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("TemperatureDescriptionRetrofit")
    fun provideTemperatureDescriptionRetrofit(
        @Named("TemperatureDescriptionOkHttp") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideTemperatureDescriptionApi(
        @Named("TemperatureDescriptionRetrofit") retrofit: Retrofit
    ): TemperatureDescriptionApi {
        return retrofit.create(TemperatureDescriptionApi::class.java)
    }
}
