package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentTokenUsage
import com.japanesehelper.presentation.screens.homeScreen.components.LabeledBlock
import com.japanesehelper.presentation.theme.LocalAppPadding

private const val NO_TOKEN_COUNT = "—"

/**
 * A compact, read-only readout of the real token usage the backend reported
 * for the last message sent - nothing is counted on the device. Replaced
 * (not accumulated) after every send, so it always reflects the latest
 * request only.
 */
@Composable
fun TokenUsageSection(usage: AgentTokenUsage, modifier: Modifier = Modifier) {
    LabeledBlock(caption = stringResource(R.string.ai_agent_usage_title), modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.quarter)) {
            Text(
                text = stringResource(R.string.ai_agent_usage_current_request_row, usage.currentRequestTokens.orDash()),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.ai_agent_usage_history_row, usage.historyTokens.orDash()),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.ai_agent_usage_response_row, usage.responseTokens.orDash()),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.ai_agent_usage_total_row, usage.totalTokens.orDash()),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun Int?.orDash(): String = this?.toString() ?: NO_TOKEN_COUNT
