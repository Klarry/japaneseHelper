package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentCompressionStatus

/**
 * Sits under the token usage and explains it: with compression on, the
 * numbers above cover a summary of the older turns plus the messages still
 * kept word for word, and this says how big each of those is. Both values
 * come from the backend response - nothing is counted or summarised here.
 */
@Composable
fun CompressionStatusSection(status: AgentCompressionStatus, modifier: Modifier = Modifier) {
    CompactReadout(
        caption = stringResource(R.string.ai_agent_compression_status_title),
        values = listOf(
            stringResource(R.string.ai_agent_compression_status_on),
            stringResource(R.string.ai_agent_compression_summary_row, status.summaryTokens.orDash()),
            stringResource(R.string.ai_agent_compression_recent_row, status.recentMessages)
        ),
        modifier = modifier
    )
}
