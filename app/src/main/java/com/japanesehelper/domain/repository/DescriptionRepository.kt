package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.Description

interface DescriptionRepository {
    suspend fun getDescription(meaning: String): Description
}
