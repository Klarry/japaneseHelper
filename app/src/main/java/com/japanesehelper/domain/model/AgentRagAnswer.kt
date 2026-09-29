package com.japanesehelper.domain.model

/**
 * An answer from the backend's document index, and what it was built on.
 *
 * Reported, not produced. Retrieval, the FAISS search, the embeddings and
 * the prompt that carries the chunks all happen on the backend; the device
 * sends a question with a flag and draws the answer, the file names behind
 * it and how many chunks were used.
 *
 * ``sources`` and ``chunks`` are empty when retrieval was off - nothing is
 * shown that was not retrieved.
 */
data class AgentRagAnswer(
    val answer: String,
    val ragEnabled: Boolean = false,
    val sources: List<String> = emptyList(),
    val chunks: List<AgentRagChunk> = emptyList(),
    val topK: Int = 0,
    val embeddingModel: String = "",
    val retrievalSeconds: Double = 0.0,
    val llmSeconds: Double = 0.0
)

/** One chunk the backend says it used: which file, which section, how close. */
data class AgentRagChunk(
    val chunkId: String,
    val file: String,
    val section: String = "",
    val score: Double = 0.0
)

/** What the screen keeps next to an answer that came from the index. */
data class AgentRagInfo(
    val sources: List<String>,
    val chunkCount: Int,
    val topK: Int = 0
)
