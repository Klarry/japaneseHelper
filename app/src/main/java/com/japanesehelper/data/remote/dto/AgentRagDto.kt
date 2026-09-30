package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

/** POST /agent/rag.
 *
 * ``mode`` picks the backend pipeline: "baseline" is the Day 22 search,
 * "enhanced" adds the query rewrite, the similarity filter and the reranker.
 * ``use_rag`` is sent alongside it and kept consistent, so a backend that
 * predates Day 23 still understands the request.
 *
 * The thresholds and the top-k values of the second stage are deliberately
 * not sent: they are the backend's configuration, and a phone has no business
 * deciding what counts as relevant.
 */
data class AgentRagRequestDto(
    @SerializedName("question") val question: String,
    @SerializedName("use_rag") val useRag: Boolean,
    @SerializedName("top_k") val topK: Int,
    @SerializedName("mode") val mode: String
)

/** What came back: the answer, and - only when retrieval ran - where it came
 * from. The chunks' text is not sent; the screen names sources, it does not
 * display documents. ``debug`` arrives for the enhanced mode only. */
data class AgentRagResponseDto(
    @SerializedName("answer") val answer: String?,
    @SerializedName("rag_enabled") val ragEnabled: Boolean?,
    @SerializedName("mode") val mode: String?,
    @SerializedName("sources") val sources: List<String>?,
    @SerializedName("retrieved_chunks") val retrievedChunks: List<AgentRagChunkDto>?,
    @SerializedName("top_k") val topK: Int?,
    @SerializedName("embedding_model") val embeddingModel: String?,
    @SerializedName("retrieval_seconds") val retrievalSeconds: Double?,
    @SerializedName("llm_seconds") val llmSeconds: Double?,
    @SerializedName("debug") val debug: AgentRagDebugDto?
)

data class AgentRagChunkDto(
    @SerializedName("chunk_id") val chunkId: String?,
    @SerializedName("file") val file: String?,
    @SerializedName("section") val section: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("similarity_score") val similarityScore: Double?,
    @SerializedName("keyword_score") val keywordScore: Double?,
    @SerializedName("rerank_score") val rerankScore: Double?
)

/** The funnel the backend's second stage put the question through (Day 23). */
data class AgentRagDebugDto(
    @SerializedName("original_query") val originalQuery: String?,
    @SerializedName("rewritten_query") val rewrittenQuery: String?,
    @SerializedName("rewrite_used") val rewriteUsed: String?,
    @SerializedName("retrieval_top_k") val retrievalTopK: Int?,
    @SerializedName("retrieved_count") val retrievedCount: Int?,
    @SerializedName("filtered_count") val filteredCount: Int?,
    @SerializedName("final_count") val finalCount: Int?,
    @SerializedName("threshold") val threshold: Double?,
    @SerializedName("reordered") val reordered: Boolean?
)
