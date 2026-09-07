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
import com.japanesehelper.presentation.screens.aiAgentScreen.components.AskAgentButton
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.LabeledBlock
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenScaffold
import com.japanesehelper.presentation.viewmodel.AiAgentViewModel
import com.japanesehelper.presentation.viewmodel.screendata.AgentChatUiState

/**
 * Day 6: a minimal single-request demo of the backend's JapaneseLearningAgent.
 * The user's message is sent to POST /agent/chat unchanged - no prompt is
 * built here. Nothing is requested until "Ask Agent" is tapped.
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

        OutlinedTextField(
            value = state.message,
            onValueChange = viewModel::onMessageChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.ai_agent_input_placeholder)) },
            minLines = 3
        )

        AskAgentButton(
            canAsk = state.message.isNotBlank(),
            isLoading = state.result is AgentChatUiState.Loading,
            onAsk = viewModel::ask
        )

        when (val result = state.result) {
            is AgentChatUiState.Idle, is AgentChatUiState.Loading -> Unit

            is AgentChatUiState.Error ->
                ErrorWithRetry(message = result.message, onRetry = viewModel::ask)

            is AgentChatUiState.Success ->
                LabeledBlock(caption = stringResource(R.string.ai_agent_response_label)) {
                    Text(text = result.response, style = MaterialTheme.typography.bodyLarge)
                }
        }
    }
}
