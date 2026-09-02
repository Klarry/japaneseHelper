package com.japanesehelper.data.remote.dto

data class KanjiWordSetResponseDto(
    val prompt: String,
    val words: List<String>,
    val cost: Int,
    val value: Int
)
