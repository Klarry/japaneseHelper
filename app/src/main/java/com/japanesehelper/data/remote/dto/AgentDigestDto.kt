package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

/** GET /agent/digest. Everything is nullable: a backend from before Day 18
 * has no such endpoint, and a task that has never run reports zeroes. */
data class AgentDigestDto(
    @SerializedName("found") val found: Boolean?,
    @SerializedName("active") val active: Boolean?,
    @SerializedName("query") val query: String?,
    @SerializedName("interval_seconds") val intervalSeconds: Int?,
    @SerializedName("runs") val runs: Int?,
    @SerializedName("failed_runs") val failedRuns: Int?,
    @SerializedName("last_run") val lastRun: String?,
    @SerializedName("items_collected") val itemsCollected: Int?,
    @SerializedName("summary") val summary: String?
)
