package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.presentation.screens.homeScreen.components.CenteredLoadingIndicator
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.LabeledBlock
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.AgentHistoryUiState

/**
 * The whole conversation so far. Everything here comes from GET /agent/history
 * or from appending the last chat() result - Gemini is never called just to
 * display history.
 */
@Composable
fun AgentConversation(
    historyState: AgentHistoryUiState,
    onRetryLoad: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (historyState) {
        is AgentHistoryUiState.Loading -> CenteredLoadingIndicator(modifier = modifier)

        is AgentHistoryUiState.Error ->
            ErrorWithRetry(message = historyState.message, onRetry = onRetryLoad, modifier = modifier)

        is AgentHistoryUiState.Loaded ->
            if (historyState.messages.isEmpty()) {
                Text(
                    text = stringResource(R.string.ai_agent_empty_state),
                    modifier = modifier,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
                ) {
                    historyState.messages.forEach { message -> AgentMessageCard(message) }
                }
            }
    }
}

@Composable
private fun AgentMessageCard(message: AgentMessage) {
    val caption = when (message.role) {
        AgentMessageRole.USER -> stringResource(R.string.ai_agent_role_user)
        AgentMessageRole.ASSISTANT -> stringResource(R.string.ai_agent_role_agent)
    }

    LabeledBlock(caption = caption) {
        Text(text = message.content, style = MaterialTheme.typography.bodyLarge)
    }
}
