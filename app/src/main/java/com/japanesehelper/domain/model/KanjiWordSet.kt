package com.japanesehelper.domain.model

/** [prompt] is built by the backend and only displayed here, never constructed on Android. */
data class KanjiWordSet(
    val prompt: String,
    val words: List<String>,
    val cost: Int,
    val value: Int
)
