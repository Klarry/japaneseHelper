package com.japanesehelper.domain.model

data class RandomWord(
    val word: String,
    val furigana: String? = null,
    val romaji: String? = null,
    val meaning: String? = null,
)