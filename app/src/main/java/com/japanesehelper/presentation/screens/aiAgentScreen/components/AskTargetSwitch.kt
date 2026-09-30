package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.AskTarget

/**
 * Where the next question goes: to the learning agent as before, or to the
 * backend's document index - and if to the index, through which of its three
 * pipelines.
 *
 * A row of chips and nothing else. The device does no retrieval, no
 * rewriting, no filtering and no reranking, and knows nothing about the
 * index: this names one mode on one request, and the backend does the rest.
 *
 * The row scrolls sideways rather than wrapping, so a narrow phone shortens
 * the row instead of clipping a chip.
 */
@Composable
fun AskTargetSwitch(
    target: AskTarget,
    onChanged: (AskTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.ai_agent_ask_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AskTarget.entries.forEach { option ->
            FilterChip(
                selected = target == option,
                onClick = { onChanged(option) },
                label = { Text(stringResource(option.labelRes())) }
            )
        }
    }
}

/** The chip's caption. Kept next to the switch rather than on the enum: the
 * enum says what a question does, and has no business knowing about
 * resources. */
private fun AskTarget.labelRes(): Int = when (this) {
    AskTarget.AGENT -> R.string.ai_agent_ask_agent
    AskTarget.RAG_OFF -> R.string.ai_agent_rag_off
    AskTarget.RAG_BASELINE -> R.string.ai_agent_rag_baseline
    AskTarget.RAG_ENHANCED -> R.string.ai_agent_rag_enhanced
}
