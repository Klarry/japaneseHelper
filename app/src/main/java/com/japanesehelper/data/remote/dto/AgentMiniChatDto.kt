package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

/** POST /agent/mini-chat (Day 25).
 *
 * Only the message. The history, the task memory, which documents to
 * retrieve and how many all belong to the backend - a client that could set
 * them could also contradict them.
 */
data class AgentMiniChatRequestDto(
    @SerializedName("message") val message: String
)

/** The answer, its evidence, and the state of the conversation it belongs to. */
data class AgentMiniChatResponseDto(
    @SerializedName("answer") val answer: String?,
    @SerializedName("rag_status") val ragStatus: String?,
    @SerializedName("confidence") val confidence: String?,
    @SerializedName("sources") val sources: List<AgentRagSourceDto>?,
    @SerializedName("citations") val citations: List<AgentRagCitationDto>?,
    @SerializedName("task_memory") val taskMemory: AgentTaskMemoryDto?,
    @SerializedName("memory_changes") val memoryChanges: List<String>?,
    @SerializedName("retrieved_count") val retrievedCount: Int?,
    @SerializedName("filtered_count") val filteredCount: Int?,
    @SerializedName("final_count") val finalCount: Int?,
    @SerializedName("best_relevance") val bestRelevance: Double?,
    @SerializedName("answer_threshold") val answerThreshold: Double?,
    @SerializedName("history_length") val historyLength: Int?,
    @SerializedName("seconds") val seconds: Double?
)

/** What the conversation has settled, as the backend reports it (Day 25). */
data class AgentTaskMemoryDto(
    @SerializedName("goal") val goal: String?,
    @SerializedName("confirmed_terms") val confirmedTerms: List<String>?,
    @SerializedName("constraints") val constraints: List<String>?,
    @SerializedName("decisions") val decisions: List<String>?,
    @SerializedName("requirements") val requirements: List<String>?,
    @SerializedName("current_state") val currentState: String?
)
