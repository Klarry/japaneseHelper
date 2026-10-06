package com.japanesehelper.di

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Shared setup for the Gemini-backed endpoints. Backend latency is variable -
 * observed from ~12s to beyond 30s - so read/call timeouts carry a wide margin.
 */
internal object BackendHttp {

    /**
     * Where this build's backend is.
     *
     * Two entries rather than one address and a comment, so switching is
     * one word and neither address has to be looked up again. Whichever is
     * chosen must also be listed in `network_security_config.xml`:
     * cleartext is permitted per address, and without the entry the request
     * never leaves the phone.
     */
    private enum class Backend(val url: String) {
        /** The deployed one. Everything Gemini-backed is served from here. */
        VPS("http://89.167.34.196:8000/"),

        /** The same backend running on the laptop (Day 27). The local model
         * lives on that machine and Ollama binds to its localhost, so the
         * backend in front of it has to be there too - the VPS has neither a
         * copy of it nor a GPU worth running one on. */
        LAPTOP("http://192.168.10.35:8000/")
    }

    private val backend = Backend.LAPTOP

    val BASE_URL = backend.url

    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val WRITE_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 60L
    private const val CALL_TIMEOUT_SECONDS = 90L

    fun clientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(RetryReadsInterceptor())

    fun retrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}

/**
 * Retries a read that failed on the way there, rather than showing an empty
 * screen for a connection the phone can simply try again.
 *
 * Only GET is retried, and deliberately so: repeating a POST would ask the
 * agent the same question twice, and repeating a DELETE would delete
 * something that came back in between. A request that reached the backend
 * and was answered - with anything, including an error - is not retried
 * either; the answer is the answer.
 */
private class RetryReadsInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.method != "GET") return chain.proceed(request)

        var failure: IOException? = null

        repeat(READ_ATTEMPTS) { attempt ->
            try {
                return chain.proceed(request)
            } catch (e: IOException) {
                failure = e
                if (attempt < READ_ATTEMPTS - 1) {
                    Thread.sleep(RETRY_DELAY_MILLIS * (attempt + 1))
                }
            }
        }

        throw failure ?: IOException("Request failed")
    }

    private companion object {
        const val READ_ATTEMPTS = 3
        const val RETRY_DELAY_MILLIS = 400L
    }
}
