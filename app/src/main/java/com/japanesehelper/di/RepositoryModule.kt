package com.japanesehelper.di

import androidx.datastore.core.DataStore
import com.japanesehelper.android.datastore.VocabPreferences
import com.japanesehelper.data.remote.api.GoogleSearchApi
import com.japanesehelper.data.remote.api.VocabApi
import com.japanesehelper.data.repository.GoogleSearchRepositoryImpl
import com.japanesehelper.data.repository.VocabRepositoryImpl
import com.japanesehelper.domain.repository.GoogleSearchRepository
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
    fun provideGoogleSearchRepository(
        api: GoogleSearchApi,
    ): GoogleSearchRepository {
        return GoogleSearchRepositoryImpl(api)
    }
}
