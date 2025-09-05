package com.japanesehelper.domain.repository

import com.japanesehelper.android.datastore.VocabPreferences
import com.japanesehelper.domain.model.RandomWord
import com.japanesehelper.presentation.viewmodel.screendata.LevelState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface VocabRepository {
    val vocabPreferencesFlow: Flow<VocabPreferences>

    suspend fun getRandomWord(): RandomWord

    suspend fun updateJLPTLevel(level: LevelState)

    fun getJLPTLevel(): Flow<LevelState>
}
