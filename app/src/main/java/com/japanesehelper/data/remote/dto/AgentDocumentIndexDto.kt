package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

/** GET /agent/documents. Everything is nullable: a backend from before
 * Day 21 has no such endpoint, and a backend that has one may not have had
 * an index built yet. */
data class AgentDocumentIndexDto(
    @SerializedName("found") val found: Boolean?,
    @SerializedName("documents") val documents: Int?,
    @SerializedName("total_characters") val totalCharacters: Int?,
    @SerializedName("fixed_chunks") val fixedChunks: Int?,
    @SerializedName("structural_chunks") val structuralChunks: Int?,
    @SerializedName("embedding_model") val embeddingModel: String?,
    @SerializedName("embedding_dimension") val embeddingDimension: Int?,
    @SerializedName("built_at") val builtAt: String?
)
