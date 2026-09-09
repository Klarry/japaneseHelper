package com.japanesehelper.presentation.screens.aiAgentScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.japanesehelper.R
import com.japanesehelper.presentation.screens.aiAgentScreen.components.AgentConversation
import com.japanesehelper.presentation.screens.aiAgentScreen.components.AskAgentButton
import com.japanesehelper.presentation.screens.aiAgentScreen.components.ClearHistoryButton
import com.japanesehelper.presentation.screens.aiAgentScreen.components.TokenUsageSection
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenScaffold
import com.japanesehelper.presentation.viewmodel.AiAgentViewModel
import com.japanesehelper.presentation.viewmodel.screendata.AgentHistoryUiState

/**
 * A simple, persistent chat with the backend's JapaneseLearningAgent.
 * History is loaded from GET /agent/history when the screen opens and
 * restored as-is; every send appends to it; "Clear History" empties it via
 * DELETE /agent/history. The user's message is always sent to POST
 * /agent/chat unchanged - no prompt is built here, that belongs to the
 * backend.
 */
@Composable
fun AiAgentScreen(
    navController: NavController,
    viewModel: AiAgentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ScreenScaffold(
        title = stringResource(R.string.ai_agent_title),
        onBack = { navController.popBackStack() }
    ) {
        Text(
            text = stringResource(R.string.ai_agent_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AgentConversation(
            historyState = state.history,
            onRetryLoad = viewModel::loadHistory
        )

        OutlinedTextField(
            value = state.message,
            onValueChange = viewModel::onMessageChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.ai_agent_input_placeholder)) },
            minLines = 3
        )

        AskAgentButton(
            canAsk = state.message.isNotBlank(),
            isLoading = state.isSending,
            onAsk = viewModel::send
        )

        val sendError = state.sendError
        if (sendError != null) {
            ErrorWithRetry(message = sendError, onRetry = viewModel::send)
        }

        val lastUsage = state.lastUsage
        if (lastUsage != null) {
            TokenUsageSection(usage = lastUsage)
        }

        val loadedMessages = (state.history as? AgentHistoryUiState.Loaded)?.messages
        ClearHistoryButton(
            enabled = !loadedMessages.isNullOrEmpty(),
            isLoading = state.isClearingHistory,
            onClear = viewModel::clearHistory
        )

        val clearError = state.clearHistoryError
        if (clearError != null) {
            ErrorWithRetry(message = clearError, onRetry = viewModel::clearHistory)
        }
    }
}
