package com.japanesehelper.di

import com.japanesehelper.data.remote.api.AgentApi
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
object AgentNetworkModule {

    @Provides
    @Singleton
    @Named("AgentOkHttp")
    fun provideAgentOkHttpClient(): OkHttpClient = BackendHttp.clientBuilder().build()

    @Provides
    @Singleton
    @Named("AgentRetrofit")
    fun provideAgentRetrofit(
        @Named("AgentOkHttp") okHttpClient: OkHttpClient
    ): Retrofit = BackendHttp.retrofit(okHttpClient)

    @Provides
    @Singleton
    fun provideAgentApi(
        @Named("AgentRetrofit") retrofit: Retrofit
    ): AgentApi = retrofit.create(AgentApi::class.java)
}
