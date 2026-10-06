package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

/** POST /local-chat (Day 27).
 *
 * Only the message. The model, the provider and the endpoint behind it are
 * the backend's - a device that could set them could also claim an answer
 * was local when it was not.
 */
data class LocalChatRequestDto(
    @SerializedName("message") val message: String
)

/** What the local model said, and who it was. */
data class LocalChatResponseDto(
    @SerializedName("response") val response: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("provider") val provider: String?,
    @SerializedName("seconds") val seconds: Double?
)

/** GET /local-llm/health - what the status line is drawn from. */
data class LocalLlmHealthDto(
    @SerializedName("available") val available: Boolean?,
    @SerializedName("model") val model: String?,
    @SerializedName("model_installed") val modelInstalled: Boolean?,
    @SerializedName("models") val models: List<String>?,
    @SerializedName("error") val error: String?
)

/** The 503 body when the local model cannot answer.
 *
 * [error] is the line to show. [detail] is kept because it is the one that
 * says what to do about it - and there is no field for a cloud answer,
 * because the backend does not send one. */
data class LocalLlmErrorDto(
    @SerializedName("error") val error: String?,
    @SerializedName("reason") val reason: String?,
    @SerializedName("detail") val detail: String?
)
