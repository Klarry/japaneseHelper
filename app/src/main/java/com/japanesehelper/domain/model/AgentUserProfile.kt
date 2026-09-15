package com.japanesehelper.domain.model

/**
 * How the learner wants to be answered: level, style, length, and which
 * language Japanese is translated into.
 *
 * This is a setting, not a memory layer and not part of the conversation.
 * The device only displays it and sends changes - applying it to a request
 * is entirely the backend's job, and the prompt is never touched here.
 */
data class AgentUserProfile(
    val japaneseLevel: String = "",
    val explanationStyle: String = "",
    val answerFormat: String = "",
    val translationLanguage: String = ""
)

/** The values the screen offers for each setting. */
object AgentProfileOptions {
    val JAPANESE_LEVELS = listOf("N5", "N4", "N3", "N2", "N1")
    val EXPLANATION_STYLES = listOf("simple", "detailed")
    val ANSWER_FORMATS = listOf("short", "detailed")
    val TRANSLATION_LANGUAGES = listOf("Russian", "English")
}

/**
 * The two profiles the comparison is made with: the same question asked
 * under each should come back answered differently.
 */
enum class AgentProfilePreset(val label: String, val profile: AgentUserProfile) {
    A(
        label = "Profile A",
        profile = AgentUserProfile(
            japaneseLevel = "N4",
            explanationStyle = "simple",
            answerFormat = "short",
            translationLanguage = "Russian"
        )
    ),
    B(
        label = "Profile B",
        profile = AgentUserProfile(
            japaneseLevel = "N2",
            explanationStyle = "detailed",
            answerFormat = "detailed",
            translationLanguage = "English"
        )
    );

    companion object {
        /** Which preset the profile currently matches, if either. */
        fun matching(profile: AgentUserProfile?): AgentProfilePreset? =
            entries.firstOrNull { it.profile == profile }
    }
}
