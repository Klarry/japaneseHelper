package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.ModelComparison

interface ModelComparisonRepository {
    suspend fun compareModels(kanji: String, prompt: String, models: List<String>): ModelComparison
}
