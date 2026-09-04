package com.japanesehelper.di

import com.japanesehelper.data.remote.api.ModelComparisonApi
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
object ModelComparisonNetworkModule {

    @Provides
    @Singleton
    @Named("ModelComparisonOkHttp")
    fun provideModelComparisonOkHttpClient(): OkHttpClient = BackendHttp.clientBuilder().build()

    @Provides
    @Singleton
    @Named("ModelComparisonRetrofit")
    fun provideModelComparisonRetrofit(
        @Named("ModelComparisonOkHttp") okHttpClient: OkHttpClient
    ): Retrofit = BackendHttp.retrofit(okHttpClient)

    @Provides
    @Singleton
    fun provideModelComparisonApi(
        @Named("ModelComparisonRetrofit") retrofit: Retrofit
    ): ModelComparisonApi = retrofit.create(ModelComparisonApi::class.java)
}
