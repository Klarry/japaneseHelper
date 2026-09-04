package com.japanesehelper.di

import androidx.datastore.core.DataStore
import com.japanesehelper.android.datastore.VocabPreferences
import com.japanesehelper.data.remote.api.DescriptionApi
import com.japanesehelper.data.remote.api.ImageSearchApi
import com.japanesehelper.data.remote.api.KanjiWordSetApi
import com.japanesehelper.data.remote.api.ModelComparisonApi
import com.japanesehelper.data.remote.api.TemperatureDescriptionApi
import com.japanesehelper.data.remote.api.VocabApi
import com.japanesehelper.data.repository.DescriptionRepositoryImpl
import com.japanesehelper.data.repository.ImageSearchRepositoryImpl
import com.japanesehelper.data.repository.KanjiWordSetRepositoryImpl
import com.japanesehelper.data.repository.ModelComparisonRepositoryImpl
import com.japanesehelper.data.repository.TemperatureDescriptionRepositoryImpl
import com.japanesehelper.data.repository.VocabRepositoryImpl
import com.japanesehelper.domain.repository.DescriptionRepository
import com.japanesehelper.domain.repository.ImageSearchRepository
import com.japanesehelper.domain.repository.KanjiWordSetRepository
import com.japanesehelper.domain.repository.ModelComparisonRepository
import com.japanesehelper.domain.repository.TemperatureDescriptionRepository
import com.japanesehelper.domain.repository.VocabRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideVocabRepository(
        api: VocabApi,
        dataStore: DataStore<VocabPreferences>
    ): VocabRepository {
        return VocabRepositoryImpl(
            vocabApi = api,
            vocabDataStore = dataStore
        )
    }

    @Provides
    @Singleton
    fun provideImageSearchRepository(
        api: ImageSearchApi,
    ): ImageSearchRepository {
        return ImageSearchRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideDescriptionRepository(
        api: DescriptionApi,
    ): DescriptionRepository {
        return DescriptionRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideKanjiWordSetRepository(
        api: KanjiWordSetApi,
    ): KanjiWordSetRepository {
        return KanjiWordSetRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideTemperatureDescriptionRepository(
        api: TemperatureDescriptionApi,
    ): TemperatureDescriptionRepository {
        return TemperatureDescriptionRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideModelComparisonRepository(
        api: ModelComparisonApi,
    ): ModelComparisonRepository {
        return ModelComparisonRepositoryImpl(api)
    }
}
