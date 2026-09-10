package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R

/**
 * Chooses which mode the next message is sent in, in the same TabRow style
 * the Kanji Word Set screen uses to pick an experiment type. The screen only
 * makes the choice: summarising the older messages, and deciding when to do
 * it, is entirely the backend agent's job.
 */
@Composable
fun CompressionModeTabRow(
    compressionEnabled: Boolean,
    onCompressionEnabledChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = if (compressionEnabled) 1 else 0,
        modifier = modifier
    ) {
        Tab(
            selected = !compressionEnabled,
            onClick = { onCompressionEnabledChanged(false) },
            text = { Text(stringResource(R.string.ai_agent_compression_off)) }
        )
        Tab(
            selected = compressionEnabled,
            onClick = { onCompressionEnabledChanged(true) },
            text = { Text(stringResource(R.string.ai_agent_compression_on)) }
        )
    }
}
