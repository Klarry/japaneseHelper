package com.japanesehelper.domain.model

/**
 * Result of one Kanji Word Set experiment.
 *
 * @param prompt The actual prompt the backend sent to the LLM for this
 * experiment - never built on Android, only ever displayed as received.
 * @param words The words selected by the backend's 0/1 Knapsack solution.
 * @param cost Total cost of the selected words.
 * @param value Total value of the selected words.
 */
data class KanjiWordSet(
    val prompt: String,
    val words: List<String>,
    val cost: Int,
    val value: Int
)
