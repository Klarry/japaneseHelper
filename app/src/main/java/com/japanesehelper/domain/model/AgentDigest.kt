package com.japanesehelper.domain.model

/**
 * What the backend's periodic task has collected so far.
 *
 * Reported, not produced: the schedule, the runs, the calls to the Japanese
 * API and the aggregation all happen on the backend. The device asks for
 * this the same way it asks for the task state or the invariants, and draws
 * the numbers it gets.
 */
data class AgentDigest(
    val found: Boolean = false,
    val active: Boolean = false,
    val query: String = "",
    val intervalSeconds: Int = 0,
    val runs: Int = 0,
    val failedRuns: Int = 0,
    val lastRun: String = "",
    val itemsCollected: Int = 0,
    val summary: String = ""
)
