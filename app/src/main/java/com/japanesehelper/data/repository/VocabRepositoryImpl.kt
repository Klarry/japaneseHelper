package com.japanesehelper.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.japanesehelper.android.datastore.LevelJLPT
import com.japanesehelper.android.datastore.VocabPreferences
import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.api.VocabApi
import com.japanesehelper.domain.model.RandomWord
import com.japanesehelper.domain.repository.VocabRepository
import com.japanesehelper.presentation.viewmodel.screendata.LevelState
import com.japanesehelper.tools.extensions.toEnumOr
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VocabRepositoryImpl @Inject constructor(
    private val vocabApi: VocabApi,
    private val vocabDataStore: DataStore<VocabPreferences>
) : VocabRepository {

    companion object {
        val TAG: String = VocabRepositoryImpl::class.java.simpleName
    }

    override val vocabPreferencesFlow: Flow<VocabPreferences>
        get() = vocabDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.e(TAG, "Error reading sort order preferences.", exception)
                    emit(VocabPreferences.getDefaultInstance())
                } else {
                    throw exception
                }
            }

    override suspend fun getRandomWord(): RandomWord {
        return vocabApi.getRandomWord().toDomain()
    }

    override suspend fun updateJLPTLevel(level: LevelState) {
        vocabDataStore.updateData { preferences ->
            preferences
                .toBuilder()
                .setLevel(level.name.toEnumOr(LevelJLPT.RANDOM))
                .build()
        }
    }

    override fun getJLPTLevel(): Flow<LevelState> {
        return vocabPreferencesFlow
            .map { it.level.name.toEnumOr(LevelState.RANDOM) }
    }
}
