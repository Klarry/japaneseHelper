package com.japanesehelper.di

import com.japanesehelper.data.remote.api.DescriptionApi
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
 * Networking for `POST /description`.
 *
 * Same backend, base URL and timeouts as the image-search client, but on its own
 * OkHttp instance with no logging interceptors: the description flow is traced by
 * the single `ImageDescription` demo block in the ViewModel, so the image-search
 * interceptor and OkHttp's own logs would only interleave noise into it.
 */
@Module
@InstallIn(SingletonComponent::class)
object DescriptionNetworkModule {

    private const val BASE_URL = "http://89.167.34.196:8000/"

    // Matches the image-search client: the backend calls Gemini, so latency is variable.
    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val WRITE_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 60L
    private const val CALL_TIMEOUT_SECONDS = 90L

    @Provides
    @Singleton
    @Named("DescriptionOkHttp")
    fun provideDescriptionOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("DescriptionRetrofit")
    fun provideDescriptionRetrofit(
        @Named("DescriptionOkHttp") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideDescriptionApi(@Named("DescriptionRetrofit") retrofit: Retrofit): DescriptionApi {
        return retrofit.create(DescriptionApi::class.java)
    }
}
