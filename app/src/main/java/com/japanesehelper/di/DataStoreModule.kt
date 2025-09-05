package com.japanesehelper.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.japanesehelper.android.datastore.VocabPreferences
import com.japanesehelper.data.datastore.VocabPreferenceSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    const val PREF_FILE_NAME = "vocab_prefs.pb"

    @Provides
    @Singleton
    fun provideVocabPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<VocabPreferences> {
        return DataStoreFactory.create(
            serializer = VocabPreferenceSerializer,
            produceFile = { context.dataStoreFile(PREF_FILE_NAME) }
        )
    }
}
