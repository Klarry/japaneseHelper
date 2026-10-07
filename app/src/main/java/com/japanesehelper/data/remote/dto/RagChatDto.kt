package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

/** POST /rag/chat (Day 28).
 *
 * A question and which model should answer it. Nothing about retrieval:
 * the index, the filter, the reranker and the relevance gate are the
 * backend's, and they are the same ones in both modes.
 */
data class RagChatRequestDto(
    @SerializedName("message") val message: String,
    @SerializedName("provider") val provider: String
)

/** The Day 24 RAG response with the Day 28 additions: who answered, and
 * where the time went. */
data class RagChatResponseDto(
    @SerializedName("answer") val answer: String?,
    @SerializedName("sources") val sources: List<AgentRagSourceDto>?,
    @SerializedName("citations") val citations: List<AgentRagCitationDto>?,
    @SerializedName("confidence") val confidence: String?,
    @SerializedName("rag_status") val ragStatus: String?,
    @SerializedName("provider") val provider: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("retrieved_count") val retrievedCount: Int?,
    @SerializedName("final_count") val finalCount: Int?,
    @SerializedName("best_relevance") val bestRelevance: Double?,
    @SerializedName("answer_threshold") val answerThreshold: Double?,
    @SerializedName("timing") val timing: RagChatTimingDto?
)

/** Where the time went. Retrieval is the half both providers share;
 * generation is the half that differs. */
data class RagChatTimingDto(
    @SerializedName("retrieval_ms") val retrievalMs: Int?,
    @SerializedName("generation_ms") val generationMs: Int?,
    @SerializedName("total_ms") val totalMs: Int?
)
