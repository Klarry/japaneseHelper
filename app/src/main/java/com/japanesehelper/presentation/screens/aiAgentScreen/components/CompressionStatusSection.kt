package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentCompressionStatus
import com.japanesehelper.presentation.screens.homeScreen.components.LabeledBlock
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Sits under the token usage and explains it: with compression on, the
 * numbers above cover a summary of the older turns plus the messages still
 * kept word for word, and this says how big each of those is. Both values
 * come from the backend response - nothing is counted or summarised here.
 */
@Composable
fun CompressionStatusSection(status: AgentCompressionStatus, modifier: Modifier = Modifier) {
    LabeledBlock(
        caption = stringResource(R.string.ai_agent_compression_status_title),
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.quarter)) {
            Text(
                text = stringResource(R.string.ai_agent_compression_status_on),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.ai_agent_compression_summary_row, status.summaryTokens.orDash()),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.ai_agent_compression_recent_row, status.recentMessages),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
