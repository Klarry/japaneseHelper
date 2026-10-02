package com.japanesehelper.domain.model

/**
 * What the backend's conversation has settled (Day 25).
 *
 * Reported, never decided here. The goal is what the conversation was
 * started for and does not move when the talk wanders; the constraints,
 * decisions and terms are what the person pinned along the way. The device
 * holds none of this between turns - it arrives with every answer, because
 * the conversation lives in the backend's file, not in this process.
 */
data class AgentTaskMemory(
    val goal: String = "",
    val confirmedTerms: List<String> = emptyList(),
    val constraints: List<String> = emptyList(),
    val decisions: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val currentState: String = "idle"
) {
    val isEmpty: Boolean
        get() = goal.isBlank() &&
            confirmedTerms.isEmpty() &&
            constraints.isEmpty() &&
            decisions.isEmpty() &&
            requirements.isEmpty()
}

/**
 * One turn of the mini chat (Day 25).
 *
 * The answer, the documents it was built on, and the state of the
 * conversation it is part of. Everything the screen needs for one turn
 * arrives in one response: there is nothing to assemble here, and nothing
 * that could drift from what the backend believes.
 */
data class AgentMiniChatAnswer(
    val answer: String,
    val status: RagStatus = RagStatus.ANSWERED,
    val confidence: String = "",
    val sources: List<AgentRagSource> = emptyList(),
    val citations: List<AgentRagCitation> = emptyList(),
    val taskMemory: AgentTaskMemory = AgentTaskMemory(),
    /** What the last message changed in the task memory, in the backend's
     * own words. Empty when nothing did. */
    val memoryChanges: List<String> = emptyList(),
    val retrievedCount: Int = 0,
    val filteredCount: Int = 0,
    val finalCount: Int = 0,
    val bestRelevance: Double = 0.0,
    val answerThreshold: Double = 0.0,
    /** How many messages the conversation holds on the backend, after this
     * turn. The device keeps its own list only to draw it. */
    val historyLength: Int = 0
)
