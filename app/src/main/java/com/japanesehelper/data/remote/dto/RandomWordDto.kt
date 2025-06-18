package com.japanesehelper.data.remote.dto

data class RandomWordDto(
    val word: String,
    val meaning: String?,
    val furigana: String?,
    val romaji: String?,
    val level: Int?
)