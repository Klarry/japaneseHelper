package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.RagMode

/**
 * What the message box on the AI Agent screen does with the next question.
 *
 * Four states, not three, because the screen has two jobs and the assignment
 * asks for both: RAG OFF, BASELINE and ENHANCED are the three pipelines of
 * the backend's document index, compared against each other on the same
 * question through the same endpoint - and [AGENT] is the Japanese learning
 * agent itself, with its tools, its memory and its history, which has to
 * keep working whatever the index is doing.
 *
 * [ragMode] is the only thing that leaves the device: null means the
 * question goes to the agent as it always has, and anything else names one
 * of the backend's modes on a POST /agent/rag. Nothing here decides what a
 * mode means.
 */
enum class AskTarget(val ragMode: RagMode?, val isChat: Boolean = false) {
    /** The learning agent - the screen as it was before any of this. */
    AGENT(null),

    /** The index's endpoint with retrieval switched off: the same model and
     * the same question, with nothing in front of it. The honest floor of
     * the comparison. */
    RAG_OFF(RagMode.OFF),

    /** Day 22: retrieve top-k and answer from it. */
    RAG_BASELINE(RagMode.BASELINE),

    /** Day 23: rewrite the query, filter by similarity, rerank, keep the
     * best few. */
    RAG_ENHANCED(RagMode.ENHANCED),

    /** Day 25: the mini chat. The same enhanced retrieval, but as part of a
     * conversation the backend keeps - with its history and the memory of
     * what this task has settled. It has no [ragMode] because it is not a
     * mode of the one-shot endpoint: it is a different endpoint. */
    CHAT(null, isChat = true);

    /** Whether the question goes to the document index's one-shot endpoint. */
    val asksTheIndex: Boolean get() = ragMode != null

    /** Whether the question goes to the mini chat instead. */
    val asksTheChat: Boolean get() = isChat
}
