package com.japanesehelper.di

import android.util.Log
import com.japanesehelper.data.remote.api.ImageSearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageSearchNetworkModule {

    private const val BASE_URL = "http://89.167.34.196:8000/"

    // Backend search latency (Gemini + Google Image Search) is variable in practice -
    // observed anywhere from ~12s up to and beyond 30s, which was timing out ~1 in 3-4 requests.
    // Read/call timeouts carry a much larger margin to cover that variance.
    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val WRITE_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 60L
    private const val CALL_TIMEOUT_SECONDS = 90L

    private const val DEMO_LOG_TAG = "ImageSearch"
    private val DEMO_LOG_DIVIDER = "─".repeat(40)

    @Provides
    @Singleton
    @Named("ImageSearchRetrofit")
    fun provideImageSearchRetrofit(
        @Named("ImageSearchOkHttp") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @Named("ImageSearchOkHttp")
    fun provideImageSearchOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()

                Log.d(DEMO_LOG_TAG, "\u2192 ${request.method} ${request.url.encodedPath}")

                val response = chain.proceed(request)

                Log.d(DEMO_LOG_TAG, "\u2190 ${response.code} ${response.message}")

                val contentType = response.header("Content-Type").orEmpty()
                val contentLength = response.body?.contentLength() ?: -1L
                if (response.isSuccessful && contentLength >= 0) {
                    Log.d(DEMO_LOG_TAG, "\u2190 $contentType \u2022 ${formatBytesForDemoLog(contentLength)}")
                } else {
                    Log.d(DEMO_LOG_TAG, "\u2190 $contentType")
                }

                Log.d(DEMO_LOG_TAG, DEMO_LOG_DIVIDER)

                response
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideImageSearchApi(@Named("ImageSearchRetrofit") retrofit: Retrofit): ImageSearchApi {
        return retrofit.create(ImageSearchApi::class.java)
    }

    private fun formatBytesForDemoLog(bytes: Long): String {
        val kb = bytes / 1024.0
        return if (kb >= 1024) "%.1f MB".format(kb / 1024.0) else "%.0f KB".format(kb)
    }
}
