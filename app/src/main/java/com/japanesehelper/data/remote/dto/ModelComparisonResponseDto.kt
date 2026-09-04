package com.japanesehelper.data.remote.dto

data class ModelComparisonResponseDto(
    val prompt: String,
    val results: List<ModelComparisonResultDto>
)
