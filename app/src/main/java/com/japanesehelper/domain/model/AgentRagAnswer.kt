package com.japanesehelper.domain.model

/**
 * Which pipeline the backend should answer a question with (Day 23).
 *
 * The names are the backend's own, and [wire] is what goes on the request -
 * the screen does not invent modes, it names one the backend already has.
 *
 * OFF is the odd one out on purpose: it does not call the RAG endpoint at
 * all, it leaves the screen doing what it has always done - the Japanese
 * learning agent, with its tools, its memory and its history. That is the
 * honest "no retrieval" baseline for this screen, and it keeps the agent
 * reachable instead of replacing it.
 */
enum class RagMode(val wire: String) {
    OFF("off"),
    BASELINE("baseline"),
    ENHANCED("enhanced");

    /** Whether a question in this mode goes to the document index. */
    val usesIndex: Boolean get() = this != OFF

    companion object {
        /** The mode the backend says an answer was produced in. Anything
         * unrecognised is treated as the baseline rather than dropped: an
         * answer that arrived is still an answer. */
        fun from(wire: String?): RagMode =
            entries.firstOrNull { it.wire.equals(wire, ignoreCase = true) } ?: BASELINE
    }
}

/**
 * An answer from the backend's document index, and what it was built on.
 *
 * Reported, not produced. The query rewrite, the FAISS search, the
 * similarity filter, the reranking and the prompt that carries the chunks
 * all happen on the backend; the device sends a question with a mode and
 * draws what came back.
 *
 * ``sources`` and ``chunks`` are empty when nothing was retrieved - which,
 * in Enhanced mode, is also what a question the index cannot answer looks
 * like. Nothing is shown that was not retrieved.
 */
data class AgentRagAnswer(
    val answer: String,
    val ragEnabled: Boolean = false,
    val mode: RagMode = RagMode.BASELINE,
    val sources: List<String> = emptyList(),
    val chunks: List<AgentRagChunk> = emptyList(),
    val topK: Int = 0,
    val embeddingModel: String = "",
    val retrievalSeconds: Double = 0.0,
    val llmSeconds: Double = 0.0,
    /** The second stage, when there was one. Null for OFF and BASELINE,
     * because there was no funnel to report. */
    val debug: AgentRagDebug? = null
)

/** One chunk the backend says it used: which file, which section, how close.
 *
 * The three scores come from Enhanced mode only: [score] is the cosine
 * similarity FAISS found, [rerankScore] is what the backend's reranker made
 * of it. In the other modes there is no second stage, so only [score] is set.
 */
data class AgentRagChunk(
    val chunkId: String,
    val file: String,
    val section: String = "",
    val score: Double = 0.0,
    val similarityScore: Double = 0.0,
    val keywordScore: Double = 0.0,
    val rerankScore: Double = 0.0
)

/**
 * What the backend's second stage did with one question (Day 23).
 *
 * Counts, not decisions: every number here is what actually happened on the
 * backend. [retrievalTopK] and [threshold] say what was asked for; the three
 * counts say what came of it, and they only ever narrow.
 */
data class AgentRagDebug(
    val originalQuery: String = "",
    val rewrittenQuery: String = "",
    val rewriteUsed: String = "",
    val retrievalTopK: Int = 0,
    val retrievedCount: Int = 0,
    val filteredCount: Int = 0,
    val finalCount: Int = 0,
    val threshold: Double = 0.0,
    val reordered: Boolean = false
) {
    /** Whether the query that went to the search differs from the question.
     * False when the rewrite was off, or returned the question unchanged. */
    val wasRewritten: Boolean
        get() = rewrittenQuery.isNotBlank() &&
            originalQuery.isNotBlank() &&
            rewrittenQuery != originalQuery

    /** The index was searched and nothing cleared the threshold. */
    val nothingRelevant: Boolean get() = retrievedCount > 0 && finalCount == 0
}

/** What the screen keeps next to an answer that came from the index. */
data class AgentRagInfo(
    val sources: List<String>,
    val chunkCount: Int,
    val topK: Int = 0,
    val mode: RagMode = RagMode.BASELINE,
    val debug: AgentRagDebug? = null
)
