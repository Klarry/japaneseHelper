package com.japanesehelper.domain.model

/**
 * How much of the conversation the backend puts in front of the model. The
 * screen only names one - deciding what that means, and doing it, is entirely
 * the backend's job.
 */
enum class AgentContextStrategy(val wireName: String) {
    SLIDING_WINDOW("sliding_window"),
    STICKY_FACTS("sticky_facts"),
    BRANCHING("branching");

    companion object {
        fun fromWireName(wireName: String): AgentContextStrategy? =
            entries.firstOrNull { it.wireName == wireName }
    }
}
