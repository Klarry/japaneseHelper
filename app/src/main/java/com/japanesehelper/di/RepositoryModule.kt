package com.japanesehelper.di

import androidx.datastore.core.DataStore
import com.japanesehelper.android.datastore.VocabPreferences
import com.japanesehelper.data.remote.api.ImageSearchApi
import com.japanesehelper.data.remote.api.VocabApi
import com.japanesehelper.data.repository.ImageSearchRepositoryImpl
import com.japanesehelper.data.repository.VocabRepositoryImpl
import com.japanesehelper.domain.repository.ImageSearchRepository
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
}
