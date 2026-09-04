package com.japanesehelper.data.repository

import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.api.ModelComparisonApi
import com.japanesehelper.data.remote.dto.ModelComparisonRequestDto
import com.japanesehelper.domain.model.ModelComparison
import com.japanesehelper.domain.repository.ModelComparisonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ModelComparisonRepositoryImpl @Inject constructor(
    private val modelComparisonApi: ModelComparisonApi
) : ModelComparisonRepository {

    override suspend fun compareModels(
        kanji: String,
        prompt: String,
        models: List<String>
    ): ModelComparison = withContext(Dispatchers.IO) {
        modelComparisonApi.compareModels(
            ModelComparisonRequestDto(
                kanji = kanji,
                prompt = prompt,
                models = models
            )
        ).toDomain()
    }
}
