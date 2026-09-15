package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * The backend's profile. It has settings this screen does not show
 * (preferred language, free-form preferences); they are read here and left
 * alone, which is why the request below carries only the four fields the
 * screen actually owns.
 */
data class AgentUserProfileDto(
    @SerializedName("japanese_level") val japaneseLevel: String,
    @SerializedName("explanation_style") val explanationStyle: String,
    @SerializedName("answer_format") val answerFormat: String,
    @SerializedName("translation_language") val translationLanguage: String
)

/** Only the four settings the screen shows: a field the request leaves out
 * keeps its current value on the backend. */
data class AgentUserProfileRequestDto(
    @SerializedName("japanese_level") val japaneseLevel: String,
    @SerializedName("explanation_style") val explanationStyle: String,
    @SerializedName("answer_format") val answerFormat: String,
    @SerializedName("translation_language") val translationLanguage: String
)
