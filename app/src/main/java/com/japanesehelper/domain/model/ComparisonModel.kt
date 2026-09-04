package com.japanesehelper.domain.model

/** Purely descriptive; never sent to the backend - only [modelId] is. */
enum class ComparisonStrength {
    LIGHT,
    STANDARD,
    PRO
}

data class ComparisonModel(val strength: ComparisonStrength, val modelId: String)

/**
 * Single source of truth for Day 5's Model Comparison experiment. Update the
 * model IDs here (and only here) after checking which Gemini models are
 * actually available for the API key - "light/standard/pro" is just a label,
 * it is not guaranteed to match any particular Gemini model family.
 *
 * History: gemini-3.5-pro returned 404 ("model not found"); the follow-up
 * gemini-2.5-pro returned 404 too ("no longer available to new users" -
 * Gemini's own error pointed at gemini-3.1-pro-preview as the replacement).
 */
val COMPARISON_MODELS: List<ComparisonModel> = listOf(
    ComparisonModel(ComparisonStrength.LIGHT, "gemini-3.5-flash-lite"),
    ComparisonModel(ComparisonStrength.STANDARD, "gemini-3.5-flash"),
    ComparisonModel(ComparisonStrength.PRO, "gemini-3.1-pro-preview")
)
