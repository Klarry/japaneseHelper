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
import javax.inject.Named
import javax.inject.Singleton

private const val DEMO_LOG_TAG = "ImageSearch"
private const val BYTES_PER_KB = 1024.0
private val DEMO_LOG_DIVIDER = "─".repeat(40)

@Module
@InstallIn(SingletonComponent::class)
object ImageSearchNetworkModule {

    @Provides
    @Singleton
    @Named("ImageSearchRetrofit")
    fun provideImageSearchRetrofit(
        @Named("ImageSearchOkHttp") okHttpClient: OkHttpClient
    ): Retrofit = BackendHttp.retrofit(okHttpClient)

    @Provides
    @Singleton
    @Named("ImageSearchOkHttp")
    fun provideImageSearchOkHttpClient(): OkHttpClient = BackendHttp.clientBuilder()
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d(DEMO_LOG_TAG, "→ ${request.method} ${request.url.encodedPath}")

            val response = chain.proceed(request)
            Log.d(DEMO_LOG_TAG, "← ${response.code} ${response.message}")

            val contentType = response.header("Content-Type").orEmpty()
            val contentLength = response.body?.contentLength() ?: -1L
            if (response.isSuccessful && contentLength >= 0) {
                Log.d(DEMO_LOG_TAG, "← $contentType • ${formatBytesForDemoLog(contentLength)}")
            } else {
                Log.d(DEMO_LOG_TAG, "← $contentType")
            }

            Log.d(DEMO_LOG_TAG, DEMO_LOG_DIVIDER)
            response
        }
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        )
        .build()

    @Provides
    @Singleton
    fun provideImageSearchApi(
        @Named("ImageSearchRetrofit") retrofit: Retrofit
    ): ImageSearchApi = retrofit.create(ImageSearchApi::class.java)

    private fun formatBytesForDemoLog(bytes: Long): String {
        val kb = bytes / BYTES_PER_KB
        return if (kb >= BYTES_PER_KB) "%.1f MB".format(kb / BYTES_PER_KB) else "%.0f KB".format(kb)
    }
}
