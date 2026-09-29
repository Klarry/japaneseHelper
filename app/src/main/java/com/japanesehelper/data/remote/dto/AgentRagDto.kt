package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

/** POST /agent/rag. ``use_rag`` false asks the same model the same question
 * with no context at all - the other half of the comparison. */
data class AgentRagRequestDto(
    @SerializedName("question") val question: String,
    @SerializedName("use_rag") val useRag: Boolean,
    @SerializedName("top_k") val topK: Int
)

/** What came back: the answer, and - only when retrieval ran - where it came
 * from. The chunks' text is not sent; the screen names sources, it does not
 * display documents. */
data class AgentRagResponseDto(
    @SerializedName("answer") val answer: String?,
    @SerializedName("rag_enabled") val ragEnabled: Boolean?,
    @SerializedName("sources") val sources: List<String>?,
    @SerializedName("retrieved_chunks") val retrievedChunks: List<AgentRagChunkDto>?,
    @SerializedName("top_k") val topK: Int?,
    @SerializedName("embedding_model") val embeddingModel: String?,
    @SerializedName("retrieval_seconds") val retrievalSeconds: Double?,
    @SerializedName("llm_seconds") val llmSeconds: Double?
)

data class AgentRagChunkDto(
    @SerializedName("chunk_id") val chunkId: String?,
    @SerializedName("file") val file: String?,
    @SerializedName("section") val section: String?,
    @SerializedName("score") val score: Double?
)
