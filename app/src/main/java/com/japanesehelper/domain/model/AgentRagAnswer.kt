package com.japanesehelper.domain.model

/**
 * Which pipeline the backend should answer a question with (Day 23).
 *
 * The names are the backend's own, and [wire] is what goes on the request -
 * the screen does not invent modes, it names one the backend already has.
 *
 * All three go to the same endpoint, which is the point: OFF asks the same
 * model the same question with nothing in front of it, BASELINE retrieves
 * top-k first, ENHANCED puts the second stage in between. One thing changes
 * between them, so the three answers can be compared.
 *
 * Where the question goes at all - here or to the learning agent - is the
 * screen's decision, not this enum's: see AskTarget.
 */
enum class RagMode(val wire: String) {
    OFF("off"),
    BASELINE("baseline"),
    ENHANCED("enhanced");

    /** Whether anything is retrieved in this mode. Sent as ``use_rag`` so a
     * backend that predates Day 23 reads the request the same way. */
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
 * What the backend did with a question (Day 24).
 *
 * Reported, never decided here. [ANSWERED] means the index had enough to
 * answer from; [INSUFFICIENT_CONTEXT] means it did not and the backend did
 * not ask the model at all; [DISABLED] means no retrieval ran.
 */
enum class RagStatus(val wire: String) {
    ANSWERED("answered"),
    INSUFFICIENT_CONTEXT("insufficient_context"),
    DISABLED("disabled");

    /** Whether the backend refused to answer for want of evidence. There are
     * no sources and no citations in this state, and that is the report. */
    val refused: Boolean get() = this == INSUFFICIENT_CONTEXT

    companion object {
        /** Anything unrecognised reads as [ANSWERED]: an answer that arrived
         * is still an answer, and the empty evidence below says the rest. */
        fun from(wire: String?): RagStatus =
            entries.firstOrNull { it.wire.equals(wire, ignoreCase = true) } ?: ANSWERED
    }
}

/**
 * One document behind an answer, as the backend names it (Day 24).
 *
 * Built on the backend from the chunks a validated citation points at - the
 * device never assembles a source from anything else, so what is drawn here
 * is what really supported the answer.
 */
data class AgentRagSource(
    val source: String = "project",
    val file: String = "",
    val section: String = "",
    val chunkId: String = ""
)

/**
 * One exact fragment of one indexed document (Day 24).
 *
 * The backend has already checked that [quote] is a character-for-character
 * fragment of the chunk named by [chunkId]; a quote that was not is dropped
 * before it ever reaches the device. Nothing here re-checks it, and nothing
 * here would be able to.
 */
data class AgentRagCitation(
    val source: String = "",
    val section: String = "",
    val chunkId: String = "",
    val quote: String = ""
)

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
    val debug: AgentRagDebug? = null,
    /** Day 24. What the backend did, how much it trusts the result, and
     * whether the answer's claims stand on what was quoted. */
    val status: RagStatus = RagStatus.ANSWERED,
    val confidence: String = "",
    val citationSupport: String = "",
    /** The documents behind the answer, and the exact quotes from them.
     * Empty when nothing was cited - which is itself the report. */
    val citedSources: List<AgentRagSource> = emptyList(),
    val citations: List<AgentRagCitation> = emptyList()
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
    val reordered: Boolean = false,
    /** Day 24: how relevant the best surviving chunk was, and the bar it had
     * to clear before the backend would ask the model at all. */
    val bestRelevance: Double = 0.0,
    val bestSimilarity: Double = 0.0,
    val answerThreshold: Double = 0.0
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
    val debug: AgentRagDebug? = null,
    val status: RagStatus = RagStatus.ANSWERED,
    val citedSources: List<AgentRagSource> = emptyList(),
    val citations: List<AgentRagCitation> = emptyList(),
    /** Day 25: the mini chat's own report for this turn - the task memory as
     * it stands and the funnel's counts. Null for every other mode, because
     * there was no conversation to report on. */
    val chat: AgentMiniChatAnswer? = null
) {
    /** The sources to draw, structured when the backend sent them that way.
     *
     * The flat list is the Day 22 shape and is kept only so an answer from
     * an older backend still names its documents. Nothing is invented from
     * it: a flat entry becomes a source with a file name and nothing else.
     */
    val sourcesToShow: List<AgentRagSource>
        get() = citedSources.ifEmpty { sources.map { AgentRagSource(file = it) } }

    /** Whether there is anything at all to show under the answer. */
    val hasEvidence: Boolean get() = sourcesToShow.isNotEmpty() || citations.isNotEmpty()
}
