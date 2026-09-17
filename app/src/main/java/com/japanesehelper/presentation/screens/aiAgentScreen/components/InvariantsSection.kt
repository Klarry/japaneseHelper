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
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentInvariant

/**
 * The rules the agent is not allowed to break, exactly as the backend
 * reports them.
 *
 * Read-only apart from editing the rules themselves: whether a request
 * breaks one is decided on the backend, which is also where the refusal is
 * written. Nothing here inspects a message.
 */
@Composable
fun InvariantsSection(
    invariants: List<AgentInvariant>?,
    isWorking: Boolean,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReadoutCaption(stringResource(R.string.ai_agent_invariants_title))

            TextButton(onClick = onEdit, enabled = !isWorking) {
                Text(
                    text = stringResource(R.string.ai_agent_invariants_edit_button),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        val lines = invariants.orEmpty().map { it.rule }
            .ifEmpty { listOf(stringResource(R.string.ai_agent_invariants_empty)) }

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
