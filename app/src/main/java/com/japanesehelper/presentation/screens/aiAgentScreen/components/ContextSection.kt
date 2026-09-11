package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentContextStrategy
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole

private const val PREVIEW_LENGTH = 44

/**
 * What the chosen strategy is currently holding, straight from the backend's
 * context: the messages still inside the window, or the facts kept instead of
 * them. Branching says it with the branch chips above the conversation, so it
 * adds nothing here.
 */
@Composable
fun ContextSection(
    strategy: AgentContextStrategy,
    context: AgentContext?,
    modifier: Modifier = Modifier
) {
    when (strategy) {
        AgentContextStrategy.SLIDING_WINDOW -> {
            val messages = context?.messages.orEmpty()

            if (messages.isNotEmpty()) {
                CompactLines(
                    caption = stringResource(R.string.ai_agent_context_window_title),
                    lines = messages.map { it.asContextLine() },
                    modifier = modifier
                )
            }
        }

        AgentContextStrategy.STICKY_FACTS -> CompactLines(
            caption = stringResource(R.string.ai_agent_facts_title),
            lines = context?.facts.orEmpty()
                .map { (key, value) -> stringResource(R.string.ai_agent_context_row, key, value) }
                .ifEmpty { listOf(stringResource(R.string.ai_agent_facts_empty)) },
            modifier = modifier
        )

        AgentContextStrategy.BRANCHING -> Unit
    }
}

@Composable
private fun AgentMessage.asContextLine(): String {
    val who = when (role) {
        AgentMessageRole.USER -> stringResource(R.string.ai_agent_role_user)
        AgentMessageRole.ASSISTANT -> stringResource(R.string.ai_agent_role_agent)
    }

    return stringResource(R.string.ai_agent_context_row, who, content.firstLine())
}

/** One readable line per message, however the answer was formatted. */
private fun String.firstLine(): String {
    val flattened = replace(Regex("\\s+"), " ").trim()

    return if (flattened.length <= PREVIEW_LENGTH) flattened else flattened.take(PREVIEW_LENGTH).trimEnd() + "…"
}
