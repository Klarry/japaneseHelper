package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

/** POST /support/chat (Day 33): a question and the ticket it is about.
 *
 * There is no user id on purpose: the backend reads the ticket through its
 * CRM and takes the user from there, so the device cannot name an account.
 */
data class SupportChatRequestDto(
    @SerializedName("message") val message: String,
    @SerializedName("ticket_id") val ticketId: String,
    @SerializedName("provider") val provider: String
)

/** The support answer: documentation evidence (validated on the backend)
 * and, separately, the ticket record the answer was written with. */
data class SupportChatResponseDto(
    @SerializedName("request_id") val requestId: String?,
    @SerializedName("answer") val answer: String?,
    @SerializedName("ticket_id") val ticketId: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("sources") val sources: List<AgentRagSourceDto>?,
    @SerializedName("citations") val citations: List<AgentRagCitationDto>?,
    @SerializedName("ticket") val ticket: SupportTicketDto?,
    @SerializedName("ticket_facts_used") val ticketFactsUsed: List<String>?,
    @SerializedName("tools_used") val toolsUsed: List<SupportToolUseDto>?,
    @SerializedName("provider") val provider: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("timing") val timing: SupportTimingDto?
)

data class SupportTicketDto(
    @SerializedName("ticket_id") val ticketId: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("platform") val platform: String?,
    @SerializedName("app_version") val appVersion: String?,
    @SerializedName("previous_attempts") val previousAttempts: List<String>?,
    @SerializedName("user_language") val userLanguage: String?
)

/** One MCP call the backend made for this answer. */
data class SupportToolUseDto(
    @SerializedName("name") val name: String?,
    @SerializedName("ok") val ok: Boolean?,
    @SerializedName("ms") val ms: Int?
)

data class SupportTimingDto(
    @SerializedName("crm_ms") val crmMs: Int?,
    @SerializedName("retrieval_ms") val retrievalMs: Int?,
    @SerializedName("generation_ms") val generationMs: Int?,
    @SerializedName("total_ms") val totalMs: Int?
)

/** What the backend says when it does not answer: 404 ticket_not_found,
 * 409 ticket_user_not_found, 503 provider_unavailable, ... */
data class SupportChatErrorDto(
    @SerializedName("error") val error: String?,
    @SerializedName("detail") val detail: String?,
    @SerializedName("ticket_id") val ticketId: String?
)
