package com.japanesehelper.domain.model

/**
 * What the backend would send with the next request, and the state that
 * decides it. Every field is measured and maintained on the backend: the
 * device neither trims the history nor builds the facts, it displays them.
 */
data class AgentContext(
    val strategy: String,
    val branch: String,
    val branches: List<String>,
    val checkpoints: List<String>,
    val facts: Map<String, String>,
    val messages: List<AgentMessage>
)
