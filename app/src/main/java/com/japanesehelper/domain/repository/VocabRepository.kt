package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.RandomWord

interface VocabRepository {
    suspend fun getRandomWord(): RandomWord
}
