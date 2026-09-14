package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentMemory
import com.japanesehelper.domain.model.AgentMemoryLayer
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * The three memory layers, one block each, exactly as the backend reports
 * them - the device stores no memory of its own and never decides what
 * belongs in a layer.
 *
 * Each block clears only its own layer, which is the point worth seeing:
 * emptying the conversation leaves the task and the learner untouched, and
 * emptying the task leaves what is remembered about the learner alone.
 */
@Composable
fun MemoryLayersSection(
    memory: AgentMemory?,
    isWorking: Boolean,
    onClearLayer: (AgentMemoryLayer) -> Unit,
    modifier: Modifier = Modifier
) {
    val padding = LocalAppPadding.current
    val messageCount = memory?.shortTerm?.messages?.size ?: 0
    val working = memory?.working
    val longTerm = memory?.longTerm

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(padding.quarter)
    ) {
        MemoryLayerBlock(
            title = stringResource(R.string.ai_agent_memory_short_term_title),
            lines = listOf(
                pluralStringResource(R.plurals.ai_agent_memory_messages, messageCount, messageCount)
            ),
            layer = AgentMemoryLayer.SHORT_TERM,
            isWorking = isWorking,
            onClearLayer = onClearLayer
        )

        MemoryLayerBlock(
            title = stringResource(R.string.ai_agent_memory_working_title),
            lines = listOfNotNull(
                working?.goals?.labelled(stringResource(R.string.ai_agent_memory_goal)),
                working?.requirements?.labelled(stringResource(R.string.ai_agent_memory_requirement)),
                working?.constraints?.labelled(stringResource(R.string.ai_agent_memory_constraint)),
                working?.decisions?.labelled(stringResource(R.string.ai_agent_memory_decision))
            ).flatten().ifEmpty { listOf(stringResource(R.string.ai_agent_memory_empty)) },
            layer = AgentMemoryLayer.WORKING,
            isWorking = isWorking,
            onClearLayer = onClearLayer
        )

        MemoryLayerBlock(
            title = stringResource(R.string.ai_agent_memory_long_term_title),
            lines = listOfNotNull(
                longTerm?.profile?.map { (key, value) ->
                    stringResource(R.string.ai_agent_context_row, key, value)
                },
                longTerm?.preferences?.labelled(stringResource(R.string.ai_agent_memory_preference)),
                longTerm?.decisions?.labelled(stringResource(R.string.ai_agent_memory_decision)),
                longTerm?.knowledge?.labelled(stringResource(R.string.ai_agent_memory_knows))
            ).flatten().ifEmpty { listOf(stringResource(R.string.ai_agent_memory_empty)) },
            layer = AgentMemoryLayer.LONG_TERM,
            isWorking = isWorking,
            onClearLayer = onClearLayer
        )
    }
}

@Composable
private fun MemoryLayerBlock(
    title: String,
    lines: List<String>,
    layer: AgentMemoryLayer,
    isWorking: Boolean,
    onClearLayer: (AgentMemoryLayer) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReadoutCaption(title)

            TextButton(onClick = { onClearLayer(layer) }, enabled = !isWorking) {
                Text(
                    text = stringResource(R.string.ai_agent_memory_clear_button),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

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

/** "Constraint: N4" - the same shape the backend writes into the prompt, so
 * what is on screen reads like what the model is told. */
@Composable
private fun List<String>.labelled(label: String): List<String> =
    map { stringResource(R.string.ai_agent_context_row, label, it) }
