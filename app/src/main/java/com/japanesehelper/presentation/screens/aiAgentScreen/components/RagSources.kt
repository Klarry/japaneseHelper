package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentRagInfo
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Under an answer that came from the document index: which documents were
 * used, and how many chunks of them.
 *
 * The chunks' text is deliberately not here - the backend does not even send
 * it. What matters on the screen is that the answer has sources and which
 * they are; reading the documents is what the repository is for.
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
            ReadoutCaption(stringResource(R.string.ai_agent_rag_sources_title))

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
