package com.japanesehelper.di

import com.japanesehelper.data.remote.api.TemperatureDescriptionApi
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
object TemperatureDescriptionNetworkModule {

    @Provides
    @Singleton
    @Named("TemperatureDescriptionOkHttp")
    fun provideTemperatureDescriptionOkHttpClient(): OkHttpClient = BackendHttp.clientBuilder().build()

    @Provides
    @Singleton
    @Named("TemperatureDescriptionRetrofit")
    fun provideTemperatureDescriptionRetrofit(
        @Named("TemperatureDescriptionOkHttp") okHttpClient: OkHttpClient
    ): Retrofit = BackendHttp.retrofit(okHttpClient)

    @Provides
    @Singleton
    fun provideTemperatureDescriptionApi(
        @Named("TemperatureDescriptionRetrofit") retrofit: Retrofit
    ): TemperatureDescriptionApi = retrofit.create(TemperatureDescriptionApi::class.java)
}
