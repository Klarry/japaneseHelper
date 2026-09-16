package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentTaskState

/**
 * Where the task in progress has got to: the stage, the step inside it, and
 * what is expected next.
 *
 * Read-only by design. The stages and the moves between them are the
 * backend's state machine; the screen shows what it reports and can end the
 * task, nothing else.
 */
@Composable
fun TaskStateSection(
    taskState: AgentTaskState?,
    isWorking: Boolean,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lines = when {
        taskState == null || !taskState.isActive ->
            listOf(stringResource(R.string.ai_agent_task_idle))

        else -> listOfNotNull(
            stringResource(R.string.ai_agent_context_row, stringResource(R.string.ai_agent_task_stage), taskState.stage),
            taskState.currentStep.takeIf { it.isNotBlank() }?.let {
                stringResource(R.string.ai_agent_context_row, stringResource(R.string.ai_agent_task_step), it)
            },
            taskState.expectedAction.takeIf { it.isNotBlank() }?.let {
                stringResource(R.string.ai_agent_context_row, stringResource(R.string.ai_agent_task_action), it)
            }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReadoutCaption(stringResource(R.string.ai_agent_task_title))

            TextButton(onClick = onClear, enabled = !isWorking && taskState?.isActive == true) {
                Text(
                    text = stringResource(R.string.ai_agent_task_clear_button),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        lines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
