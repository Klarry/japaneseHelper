package com.japanesehelper.di

import com.japanesehelper.data.remote.api.KanjiWordSetApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KanjiWordSetNetworkModule {

    @Provides
    @Singleton
    @Named("KanjiWordSetOkHttp")
    fun provideKanjiWordSetOkHttpClient(): OkHttpClient = BackendHttp.clientBuilder().build()

    @Provides
    @Singleton
    @Named("KanjiWordSetRetrofit")
    fun provideKanjiWordSetRetrofit(
        @Named("KanjiWordSetOkHttp") okHttpClient: OkHttpClient
    ): Retrofit = BackendHttp.retrofit(okHttpClient)

    @Provides
    @Singleton
    fun provideKanjiWordSetApi(
        @Named("KanjiWordSetRetrofit") retrofit: Retrofit
    ): KanjiWordSetApi = retrofit.create(KanjiWordSetApi::class.java)
}
