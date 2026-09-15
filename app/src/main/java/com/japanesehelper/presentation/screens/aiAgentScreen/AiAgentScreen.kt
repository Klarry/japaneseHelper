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
import com.japanesehelper.domain.model.AgentContextStrategy
import com.japanesehelper.presentation.screens.aiAgentScreen.components.AgentConversation
import com.japanesehelper.presentation.screens.aiAgentScreen.components.AskAgentButton
import com.japanesehelper.presentation.screens.aiAgentScreen.components.BranchingControls
import com.japanesehelper.presentation.screens.aiAgentScreen.components.ClearHistoryButton
import com.japanesehelper.presentation.screens.aiAgentScreen.components.ContextSection
import com.japanesehelper.presentation.screens.aiAgentScreen.components.ContextStrategyTabRow
import com.japanesehelper.presentation.screens.aiAgentScreen.components.CreateBranchDialog
import com.japanesehelper.presentation.screens.aiAgentScreen.components.EditProfileDialog
import com.japanesehelper.presentation.screens.aiAgentScreen.components.MemoryLayersSection
import com.japanesehelper.presentation.screens.aiAgentScreen.components.TokenUsageSection
import com.japanesehelper.presentation.screens.aiAgentScreen.components.UserProfileSection
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
 * its own, while the input and the readouts stay put underneath it.
 *
 * The Context Strategy tabs choose how the backend assembles what it sends -
 * the newest messages only, a key-value memory of what matters, the branch
 * being talked on, or the three memory layers kept apart. Choosing is all
 * this screen does: it never trims the history, builds facts or decides what
 * belongs in a memory layer, and what it shows under the input is what the
 * backend reports it would send. The existing Token Usage block is what makes
 * the difference between the strategies visible.
 *
 * The User Profile block underneath is a setting rather than a strategy: the
 * backend applies it to every request whatever the strategy, so the learner
 * never repeats their level or format in a message. Switching between the two
 * profiles and asking the same question again is the whole comparison.
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
            ContextStrategyTabRow(
                selected = state.strategy,
                onSelected = viewModel::onStrategySelected
            )

            if (state.strategy == AgentContextStrategy.BRANCHING) {
                BranchingControls(
                    context = state.context,
                    isWorking = state.isBranchWorking,
                    onSwitchBranch = viewModel::switchBranch,
                    onCreateCheckpoint = viewModel::createCheckpoint,
                    onCreateBranch = viewModel::openNewBranch,
                    modifier = Modifier.padding(top = padding.half)
                )
            }

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
                        UserProfileSection(
                            profile = state.profile,
                            isWorking = state.isProfileWorking,
                            onPresetSelected = viewModel::applyPreset,
                            onEdit = viewModel::openProfileEditor
                        )

                        val profileError = state.profileError
                        if (profileError != null) {
                            Text(
                                text = profileError,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        val lastUsage = state.lastUsage
                        if (lastUsage != null) {
                            TokenUsageSection(usage = lastUsage)
                        }

                        ContextSection(strategy = state.strategy, context = state.context)

                        if (state.strategy == AgentContextStrategy.LAYERED_MEMORY) {
                            MemoryLayersSection(
                                memory = state.memory,
                                isWorking = state.isMemoryWorking,
                                onClearLayer = viewModel::clearMemoryLayer
                            )
                        }

                        val contextError = state.contextError
                        if (contextError != null) {
                            Text(
                                text = contextError,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
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

        val profileEditor = state.profileEditor
        if (profileEditor != null) {
            EditProfileDialog(
                profile = profileEditor.profile,
                onProfileChanged = viewModel::onProfileEdited,
                onConfirm = viewModel::confirmProfileEdit,
                onDismiss = viewModel::dismissProfileEditor
            )
        }

        val newBranch = state.newBranch
        if (newBranch != null) {
            CreateBranchDialog(
                state = newBranch,
                checkpoints = state.context?.checkpoints.orEmpty(),
                onNameChanged = viewModel::onNewBranchNameChanged,
                onCheckpointSelected = viewModel::onNewBranchCheckpointSelected,
                onConfirm = viewModel::confirmNewBranch,
                onDismiss = viewModel::dismissNewBranch
            )
        }
    }
}
