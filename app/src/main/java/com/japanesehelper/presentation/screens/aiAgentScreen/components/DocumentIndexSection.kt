package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentDocumentIndex

/**
 * How big the backend's local document index is: how many documents went
 * in, how many chunks each of the two strategies made of them, and how many
 * dimensions the embeddings have.
 *
 * Four lines, read-only, in the readouts pane with the others. Nothing about
 * indexing happens here - no documents are read, no PDF is parsed, nothing
 * is chunked or embedded on the device. The index is built by a command on
 * the backend; this is the backend's own count of what came out.
 */
@Composable
fun DocumentIndexSection(index: AgentDocumentIndex?, modifier: Modifier = Modifier) {
    val lines = when {
        index == null || !index.found -> listOf(stringResource(R.string.ai_agent_index_none))

        else -> listOfNotNull(
            stringResource(R.string.ai_agent_index_documents, index.documents),
            stringResource(R.string.ai_agent_index_fixed, index.fixedChunks),
            stringResource(R.string.ai_agent_index_structural, index.structuralChunks),
            stringResource(R.string.ai_agent_index_dimension, index.embeddingDimension),
            index.embeddingModel.takeIf { it.isNotBlank() }?.let {
                stringResource(R.string.ai_agent_index_model, it)
            }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ReadoutCaption(stringResource(R.string.ai_agent_index_title))

        lines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
