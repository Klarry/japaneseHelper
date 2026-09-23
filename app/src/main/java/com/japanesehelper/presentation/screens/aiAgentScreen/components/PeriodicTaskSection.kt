package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentDigest

/**
 * The periodic task the agent started through MCP: whether it is running,
 * how often, how many runs have happened and when the last one was.
 *
 * Read-only, and deliberately thin. The schedule, the calls to the Japanese
 * API, the storage and the aggregation are all on the backend; this is four
 * lines of what it reports, refreshed with the other readouts.
 */
@Composable
fun PeriodicTaskSection(digest: AgentDigest?, modifier: Modifier = Modifier) {
    val lines = when {
        digest == null || !digest.found -> listOf(stringResource(R.string.ai_agent_digest_none))

        else -> listOfNotNull(
            stringResource(
                R.string.ai_agent_digest_state,
                stringResource(if (digest.active) R.string.ai_agent_digest_active else R.string.ai_agent_digest_stopped)
            ),
            stringResource(R.string.ai_agent_digest_interval, digest.intervalSeconds),
            stringResource(R.string.ai_agent_digest_runs, digest.runs, digest.itemsCollected),
            digest.lastRun.takeIf { it.isNotBlank() }?.let {
                stringResource(R.string.ai_agent_digest_last_run, readable(it))
            },
            digest.query.takeIf { it.isNotBlank() }?.let {
                stringResource(R.string.ai_agent_digest_collecting, it)
            }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ReadoutCaption(stringResource(R.string.ai_agent_digest_title))

        lines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

/** "2026-09-23T17:04:08+00:00" -> "2026-09-23 17:04:08 UTC", without
 * pretending the device knows better than the backend what time it was. */
private fun readable(timestamp: String): String =
    timestamp.replace('T', ' ').take(19).let { if (it.length == 19) "$it UTC" else timestamp }
