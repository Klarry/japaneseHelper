package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.RagMode
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Where the next question goes: to the agent as before, or to the backend's
 * document index - and if to the index, through which pipeline.
 *
 * Three chips and nothing else. The device does no retrieval, no rewriting,
 * no filtering and no reranking, and knows nothing about the index: this
 * names one mode on one request, and the backend does the rest.
 */
@Composable
fun RagModeSwitch(
    mode: RagMode,
    onChanged: (RagMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.ai_agent_rag_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        RagMode.entries.forEach { option ->
            FilterChip(
                selected = mode == option,
                onClick = { onChanged(option) },
                label = { Text(stringResource(option.labelRes())) }
            )
        }
    }
}

/** The chip's caption. Kept next to the switch rather than on the enum: the
 * enum is a domain type and has no business knowing about resources. */
private fun RagMode.labelRes(): Int = when (this) {
    RagMode.OFF -> R.string.ai_agent_rag_off
    RagMode.BASELINE -> R.string.ai_agent_rag_baseline
    RagMode.ENHANCED -> R.string.ai_agent_rag_enhanced
}
