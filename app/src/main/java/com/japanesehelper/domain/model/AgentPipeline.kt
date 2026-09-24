package com.japanesehelper.domain.model

/**
 * The three MCP tools the backend runs as one chain, as the screen reads
 * them back: search, then summarize on what search found, then save_to_file
 * on that.
 *
 * Reported, not run. The device holds no MCP client, calls no tool and knows
 * nothing about the order they belong in - the backend decides that a
 * message needs the chain, runs it, and says in its answer which stages
 * happened and what they produced. This is that report, in the shape the
 * screen shows it.
 */
enum class AgentPipelineStage(val tool: String) {
    SEARCH("search"),
    SUMMARIZE("summarize"),
    SAVE("save_to_file");

    companion object {
        /** The stage a reported tool name belongs to, or null when the call
         * was an ordinary lookup rather than part of the chain. */
        fun of(tool: String): AgentPipelineStage? = values().firstOrNull { it.tool == tool }
    }
}

/** One stage the backend reported, and how it went. */
data class AgentPipelineStep(
    val stage: AgentPipelineStage,
    val ok: Boolean = true,
    val error: String = ""
)

/** One dictionary entry the first stage found. */
data class AgentPipelineWord(
    val word: String,
    val reading: String = "",
    val romaji: String = "",
    val meaning: String = "",
    val level: String = ""
)

data class AgentPipeline(
    val query: String = "",
    val steps: List<AgentPipelineStep> = emptyList(),
    /** What search found. */
    val found: List<AgentPipelineWord> = emptyList(),
    /** What summarize made of it. */
    val summary: String = "",
    /** Where save_to_file put it. */
    val fileName: String = "",
    val filePath: String = ""
) {
    /** Every stage ran and none of them failed. A chain that stopped early
     * has fewer steps, because the backend does not attempt what comes after
     * a failure. */
    val completed: Boolean
        get() = steps.size == AgentPipelineStage.values().size && steps.all { it.ok }

    /** The stage that stopped the chain, if one did. */
    val failed: AgentPipelineStep?
        get() = steps.firstOrNull { !it.ok }
}
