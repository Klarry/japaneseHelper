package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.ExperimentType
import com.japanesehelper.domain.model.KanjiWordSet

interface KanjiWordSetRepository {
    suspend fun getKanjiWordSet(kanji: String, experimentType: ExperimentType): KanjiWordSet
}
