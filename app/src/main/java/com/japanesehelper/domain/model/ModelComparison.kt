package com.japanesehelper.domain.model

/**
 * One model's result within a comparison run. [text] and the token counts are
 * null when [error] is set - that one model failed without sinking the rest.
 */
data class ModelComparisonEntry(
    val model: String,
    val text: String?,
    val responseTimeMs: Int,
    val inputTokens: Int?,
    val outputTokens: Int?,
    val error: String?
)

data class ModelComparison(
    val prompt: String,
    val results: List<ModelComparisonEntry>
)
