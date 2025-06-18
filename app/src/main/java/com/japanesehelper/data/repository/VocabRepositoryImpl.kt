package com.japanesehelper.data.repository

import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.api.VocabApi
import com.japanesehelper.domain.model.RandomWord
import com.japanesehelper.domain.repository.VocabRepository
import javax.inject.Inject

class VocabRepositoryImpl @Inject constructor(
    private val vocabApi: VocabApi
) : VocabRepository {

    override suspend fun getRandomWord(): RandomWord {
        return vocabApi.getRandomWord().toDomain()
    }
}