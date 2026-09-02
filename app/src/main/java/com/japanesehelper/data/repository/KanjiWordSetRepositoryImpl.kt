package com.japanesehelper.data.repository

import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.api.KanjiWordSetApi
import com.japanesehelper.data.remote.dto.KanjiWordSetRequestDto
import com.japanesehelper.domain.model.ExperimentType
import com.japanesehelper.domain.model.KanjiWordSet
import com.japanesehelper.domain.repository.KanjiWordSetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KanjiWordSetRepositoryImpl @Inject constructor(
    private val kanjiWordSetApi: KanjiWordSetApi
) : KanjiWordSetRepository {

    override suspend fun getKanjiWordSet(
        kanji: String,
        experimentType: ExperimentType
    ): KanjiWordSet = withContext(Dispatchers.IO) {
        kanjiWordSetApi.getKanjiWordSet(
            KanjiWordSetRequestDto(
                kanji = kanji,
                experimentType = experimentType.name
            )
        ).toDomain()
    }
}
