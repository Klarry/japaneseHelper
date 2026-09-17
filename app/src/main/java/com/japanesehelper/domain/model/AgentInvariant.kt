package com.japanesehelper.domain.model

/**
 * One rule the agent is not allowed to break.
 *
 * Checking a request against these is entirely the backend's job - the
 * device shows the rules and sends changes, and never decides whether
 * something is allowed.
 */
data class AgentInvariant(
    val id: String,
    val category: AgentInvariantCategory,
    val rule: String
)

/** The four kinds of rule the backend's invariants layer is made of. */
enum class AgentInvariantCategory(val wireName: String, val label: String) {
    ARCHITECTURE("architecture", "Architecture"),
    TECHNOLOGY_STACK("technology_stack", "Tech stack"),
    TECHNICAL_DECISIONS("technical_decisions", "Decisions"),
    BUSINESS_RULES("business_rules", "Business");

    companion object {
        fun fromWireName(wireName: String): AgentInvariantCategory? =
            entries.firstOrNull { it.wireName == wireName }
    }
}
