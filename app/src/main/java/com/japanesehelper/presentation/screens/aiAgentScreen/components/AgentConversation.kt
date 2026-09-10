package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.presentation.screens.homeScreen.components.CenteredLoadingIndicator
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.MarkdownText
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.AgentHistoryUiState

/** How much of the width one of the learner's own messages may take up. */
private const val USER_BUBBLE_MAX_WIDTH_FRACTION = 0.85f

/**
 * The conversation itself: one scrolling list of messages that fills whatever
 * height the screen gives it, so the newest turn stays in view and the input
 * below never scrolls away.
 *
 * Everything shown here comes from GET /agent/history or from appending the
 * last chat() result - Gemini is never called just to display history.
 */
@Composable
fun AgentConversation(
    historyState: AgentHistoryUiState,
    onRetryLoad: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (historyState) {
        is AgentHistoryUiState.Loading -> CenteredArea(modifier) { CenteredLoadingIndicator() }

        is AgentHistoryUiState.Error -> CenteredArea(modifier) {
            ErrorWithRetry(message = historyState.message, onRetry = onRetryLoad)
        }

        is AgentHistoryUiState.Loaded ->
            if (historyState.messages.isEmpty()) {
                CenteredArea(modifier) {
                    Text(
                        text = stringResource(R.string.ai_agent_empty_state),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                MessageList(messages = historyState.messages, modifier = modifier)
            }
    }
}

/** Loading, failure and "nothing said yet" all sit in the middle of the empty
 * conversation area rather than at the top of it. */
@Composable
private fun CenteredArea(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(LocalAppPadding.current.default),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun MessageList(messages: List<AgentMessage>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()

    // Follow the conversation: a new turn scrolls itself into view, so the
    // answer is on screen without the reader chasing it.
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(LocalAppPadding.current.default),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
    ) {
        items(messages) { message ->
            when (message.role) {
                AgentMessageRole.USER -> LearnerMessage(message.content)
                AgentMessageRole.ASSISTANT -> MarkdownText(markdown = message.content)
            }
        }
    }
}

/**
 * The learner's own message: a bubble pushed to the right, which is what says
 * who wrote it - so no caption is needed. The agent's replies are the other
 * half of that contrast: full width, no bubble, Markdown rendered rather than
 * shown as raw asterisks.
 */
@Composable
private fun LearnerMessage(content: String) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .widthIn(max = maxWidth * USER_BUBBLE_MAX_WIDTH_FRACTION),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Text(
                text = content,
                modifier = Modifier.padding(
                    horizontal = LocalAppPadding.current.default,
                    vertical = LocalAppPadding.current.half
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
