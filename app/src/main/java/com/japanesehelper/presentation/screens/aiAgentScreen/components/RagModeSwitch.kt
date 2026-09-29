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
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Where the next question goes: to the agent as before, or to the backend's
 * document index.
 *
 * Two chips and nothing else. The device does no retrieval and knows nothing
 * about the index - this sets one flag on one request, and the backend does
 * the rest.
 */
@Composable
fun RagModeSwitch(enabled: Boolean, onChanged: (Boolean) -> Unit, modifier: Modifier = Modifier) {
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

        FilterChip(
            selected = !enabled,
            onClick = { onChanged(false) },
            label = { Text(stringResource(R.string.ai_agent_rag_off)) }
        )

        FilterChip(
            selected = enabled,
            onClick = { onChanged(true) },
            label = { Text(stringResource(R.string.ai_agent_rag_on)) }
        )
    }
}
