package com.japanesehelper.presentation.screens.aiAgentScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.japanesehelper.R
import com.japanesehelper.presentation.screens.aiAgentScreen.components.AgentConversation
import com.japanesehelper.presentation.screens.aiAgentScreen.components.AskAgentButton
import com.japanesehelper.presentation.screens.aiAgentScreen.components.ClearHistoryButton
import com.japanesehelper.presentation.screens.aiAgentScreen.components.CompressionModeTabRow
import com.japanesehelper.presentation.screens.aiAgentScreen.components.CompressionStatusSection
import com.japanesehelper.presentation.screens.aiAgentScreen.components.TokenUsageSection
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenTopBar
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.AiAgentViewModel
import com.japanesehelper.presentation.viewmodel.screendata.AgentHistoryUiState

/** The input grows with a long question, but never so far that it crowds out
 * the conversation above it. */
private const val MAX_INPUT_LINES = 4

/**
 * A persistent chat with the backend's JapaneseLearningAgent, laid out as one
 * chat window: the conversation owns the height of the screen and scrolls on
 * its own, while the input and the readouts stay put underneath it. Unlike
 * every other screen here it does not use ScreenScaffold, because that scrolls
 * the whole page - which would carry the input off screen as the conversation
 * grows.
 *
 * History is loaded from GET /agent/history when the screen opens and restored
 * as-is; every send appends to it; "Clear History" empties it via DELETE
 * /agent/history. The user's message is always sent to POST /agent/chat
 * unchanged - no prompt is built here, that belongs to the backend.
 *
 * The Compression tabs choose which mode that request asks for: the whole
 * history every turn, or a backend-built summary plus the newest messages.
 * Choosing is all this screen does - no summary is ever built here - and the
 * readouts under the input are what make the difference visible.
 */
@Composable
fun AiAgentScreen(
    navController: NavController,
    viewModel: AiAgentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val padding = LocalAppPadding.current
    val loadedMessages = (state.history as? AgentHistoryUiState.Loaded)?.messages

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScreenTopBar(
                title = stringResource(R.string.ai_agent_title),
                onBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
        ) {
            CompressionModeTabRow(
                compressionEnabled = state.compressionEnabled,
                onCompressionEnabledChanged = viewModel::onCompressionEnabledChanged
            )

            AgentConversation(
                historyState = state.history,
                onRetryLoad = viewModel::loadHistory,
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding.default),
                verticalArrangement = Arrangement.spacedBy(padding.half)
            ) {
                val sendError = state.sendError
                if (sendError != null) {
                    ErrorWithRetry(message = sendError, onRetry = viewModel::send)
                }

                OutlinedTextField(
                    value = state.message,
                    onValueChange = viewModel::onMessageChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.ai_agent_input_placeholder)) },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    maxLines = MAX_INPUT_LINES
                )

                AskAgentButton(
                    canAsk = state.message.isNotBlank(),
                    isLoading = state.isSending,
                    onAsk = viewModel::send
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(padding.default),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(padding.quarter)
                    ) {
                        val lastUsage = state.lastUsage
                        if (lastUsage != null) {
                            TokenUsageSection(usage = lastUsage)
                        }

                        val lastCompression = state.lastCompression
                        if (lastCompression != null && lastCompression.enabled) {
                            CompressionStatusSection(status = lastCompression)
                        }
                    }

                    ClearHistoryButton(
                        enabled = !loadedMessages.isNullOrEmpty(),
                        isLoading = state.isClearingHistory,
                        onClear = viewModel::clearHistory
                    )
                }

                val clearError = state.clearHistoryError
                if (clearError != null) {
                    ErrorWithRetry(message = clearError, onRetry = viewModel::clearHistory)
                }
            }
        }
    }
}
