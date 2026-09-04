package com.japanesehelper.data.remote.dto

data class ModelComparisonRequestDto(
    val kanji: String,
    val prompt: String,
    val models: List<String>
)
