package com.japanesehelper.domain.model

/**
 * The three layers the backend's memory is made of. They are kept apart
 * because they live for different lengths of time: short-term lasts a
 * conversation, working memory lasts a task, long-term outlives both.
 *
 * The device only names a layer - to show it, or to ask for it to be cleared.
 * What belongs in which layer is decided entirely on the backend.
 */
enum class AgentMemoryLayer(val wireName: String) {
    SHORT_TERM("short_term"),
    WORKING("working"),
    LONG_TERM("long_term")
}

/** The current conversation, as the backend reports it. */
data class AgentShortTermMemory(
    val messages: List<AgentMessage> = emptyList()
)

/** The task being worked on right now. */
data class AgentWorkingMemory(
    val goals: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val constraints: List<String> = emptyList(),
    val decisions: List<String> = emptyList()
)

/** What stays true about the learner between conversations. */
data class AgentLongTermMemory(
    val profile: Map<String, String> = emptyMap(),
    val preferences: List<String> = emptyList(),
    val decisions: List<String> = emptyList(),
    val knowledge: List<String> = emptyList()
)

data class AgentMemory(
    val shortTerm: AgentShortTermMemory = AgentShortTermMemory(),
    val working: AgentWorkingMemory = AgentWorkingMemory(),
    val longTerm: AgentLongTermMemory = AgentLongTermMemory()
)
