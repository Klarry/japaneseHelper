package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentTokenUsage

private const val NO_TOKEN_COUNT = "—"

/**
 * A compact, read-only readout of the real token usage the backend reported
 * for the last message sent - nothing is counted on the device. Replaced
 * (not accumulated) after every send, so it always reflects the latest
 * request only.
 */
@Composable
fun TokenUsageSection(usage: AgentTokenUsage, modifier: Modifier = Modifier) {
    CompactReadout(
        caption = stringResource(R.string.ai_agent_usage_title),
        values = listOf(
            stringResource(R.string.ai_agent_usage_current_request_row, usage.currentRequestTokens.orDash()),
            stringResource(R.string.ai_agent_usage_history_row, usage.historyTokens.orDash()),
            stringResource(R.string.ai_agent_usage_response_row, usage.responseTokens.orDash()),
            stringResource(R.string.ai_agent_usage_total_row, usage.totalTokens.orDash())
        ),
        modifier = modifier
    )
}

/** Shared with [CompressionStatusSection]: a count the backend could not
 * report is shown as a dash instead of being guessed at. */
internal fun Int?.orDash(): String = this?.toString() ?: NO_TOKEN_COUNT
