package com.japanesehelper.data.mapper

import com.japanesehelper.data.remote.dto.ModelComparisonResponseDto
import com.japanesehelper.data.remote.dto.ModelComparisonResultDto
import com.japanesehelper.domain.model.ModelComparison
import com.japanesehelper.domain.model.ModelComparisonEntry

fun ModelComparisonResultDto.toDomain(): ModelComparisonEntry {
    return ModelComparisonEntry(
        model = model,
        text = text,
        responseTimeMs = responseTimeMs,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        error = error
    )
}

fun ModelComparisonResponseDto.toDomain(): ModelComparison {
    return ModelComparison(
        prompt = prompt,
        results = results.map { it.toDomain() }
    )
}
