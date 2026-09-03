package com.japanesehelper.di

import com.japanesehelper.data.remote.api.DescriptionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Deliberately has no logging interceptor: the description flow is traced by the
 * single `ImageDescription` demo block in the ViewModel, and OkHttp's own logs
 * would interleave noise into it.
 */
@Module
@InstallIn(SingletonComponent::class)
object DescriptionNetworkModule {

    @Provides
    @Singleton
    @Named("DescriptionOkHttp")
    fun provideDescriptionOkHttpClient(): OkHttpClient = BackendHttp.clientBuilder().build()

    @Provides
    @Singleton
    @Named("DescriptionRetrofit")
    fun provideDescriptionRetrofit(
        @Named("DescriptionOkHttp") okHttpClient: OkHttpClient
    ): Retrofit = BackendHttp.retrofit(okHttpClient)

    @Provides
    @Singleton
    fun provideDescriptionApi(
        @Named("DescriptionRetrofit") retrofit: Retrofit
    ): DescriptionApi = retrofit.create(DescriptionApi::class.java)
}
