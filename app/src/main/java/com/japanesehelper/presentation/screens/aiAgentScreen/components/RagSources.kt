package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentRagDebug
import com.japanesehelper.domain.model.AgentRagInfo
import com.japanesehelper.domain.model.RagMode
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Under an answer that came from the document index: which pipeline produced
 * it, what the backend actually searched for, how the funnel narrowed, and
 * which documents were used.
 *
 * Everything here is reported. The counts are the backend's, the rewritten
 * query is the backend's, and the chunks' text is not here at all - the
 * backend does not even send it. What matters on the screen is that the
 * answer has sources, which they are, and how much was thrown away to get
 * to them.
 */
@Composable
fun RagSources(rag: AgentRagInfo, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(modifier = Modifier.padding(LocalAppPadding.current.half)) {
            ReadoutCaption(stringResource(rag.mode.titleRes()))

            rag.debug?.let { debug -> RagFunnel(debug) }

            Text(
                text = stringResource(R.string.ai_agent_rag_sources_title),
                modifier = Modifier.padding(top = LocalAppPadding.current.quarter),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (rag.sources.isEmpty()) {
                Text(
                    text = stringResource(R.string.ai_agent_rag_no_sources),
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                rag.sources.forEach { source ->
                    Text(
                        text = stringResource(R.string.ai_agent_rag_source_row, source),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = stringResource(R.string.ai_agent_rag_chunks, rag.chunkCount),
                modifier = Modifier.padding(top = LocalAppPadding.current.quarter),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * The second stage, in four lines: what was asked, what was searched for,
 * and how ten became three.
 *
 * Shown only when the backend sent a debug block, which it does for the
 * enhanced mode alone - the baseline has no funnel to report, and an empty
 * one would read like a funnel that did nothing.
 */
@Composable
private fun RagFunnel(debug: AgentRagDebug, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        if (debug.originalQuery.isNotBlank()) {
            Text(
                text = stringResource(R.string.ai_agent_rag_original_query, debug.originalQuery),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = if (debug.wasRewritten) {
                stringResource(R.string.ai_agent_rag_rewritten_query, debug.rewrittenQuery)
            } else {
                stringResource(R.string.ai_agent_rag_not_rewritten)
            },
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(
                R.string.ai_agent_rag_funnel,
                debug.retrievedCount,
                debug.filteredCount,
                debug.finalCount
            ),
            modifier = Modifier.padding(top = LocalAppPadding.current.quarter),
            style = MaterialTheme.typography.bodySmall
        )

        if (debug.nothingRelevant) {
            Text(
                text = stringResource(R.string.ai_agent_rag_nothing_relevant, debug.threshold),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** What the block above an answer calls itself. */
private fun RagMode.titleRes(): Int = when (this) {
    RagMode.OFF -> R.string.ai_agent_rag_mode_off
    RagMode.BASELINE -> R.string.ai_agent_rag_mode_baseline
    RagMode.ENHANCED -> R.string.ai_agent_rag_mode_enhanced
}
