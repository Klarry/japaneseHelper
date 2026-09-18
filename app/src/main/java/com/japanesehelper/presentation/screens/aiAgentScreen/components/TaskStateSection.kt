package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentTaskRefusal
import com.japanesehelper.domain.model.AgentTaskStage
import com.japanesehelper.domain.model.AgentTaskState
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Where the task in progress has got to, and the buttons that ask to move it
 * on.
 *
 * All four stages are offered at all times, including the ones the task
 * cannot move to. That is deliberate: whether a move is allowed is the
 * backend's answer, so the device asks and shows what comes back - a new
 * stage, or a refusal in the backend's own words. Nothing here checks a
 * move, disables a chip to prevent one, or guesses what will happen.
 */
@Composable
fun TaskStateSection(
    taskState: AgentTaskState?,
    refusal: AgentTaskRefusal?,
    isWorking: Boolean,
    onRequestStage: (AgentTaskStage) -> Unit,
    onApprovePlan: () -> Unit,
    onValidationPassed: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val padding = LocalAppPadding.current

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
            },
            taskState.plan.takeIf { it.isNotBlank() }?.let {
                stringResource(R.string.ai_agent_context_row, stringResource(R.string.ai_agent_task_plan), it)
            },
            taskState.nextRequirement.takeIf { it.isNotBlank() }?.let {
                stringResource(R.string.ai_agent_context_row, stringResource(R.string.ai_agent_task_requires), it)
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(padding.half)
        ) {
            AgentTaskStage.entries.forEach { stage ->
                FilterChip(
                    selected = stage.wireName == taskState?.stage,
                    onClick = { onRequestStage(stage) },
                    enabled = !isWorking,
                    label = { Text(stage.wireName) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(padding.half)) {
            TextButton(onClick = onApprovePlan, enabled = !isWorking) {
                Text(
                    text = stringResource(R.string.ai_agent_task_approve_plan_button),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            TextButton(onClick = onValidationPassed, enabled = !isWorking) {
                Text(
                    text = stringResource(R.string.ai_agent_task_validation_button),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        refusal?.let { TaskRefusal(it) }
    }
}

/**
 * The backend's refusal, line by line. Every value shown comes from the
 * answer - the screen supplies the labels and nothing else.
 */
@Composable
private fun TaskRefusal(refusal: AgentTaskRefusal) {
    val lines = listOfNotNull(
        refusal.requestedStage.takeIf { it.isNotBlank() }?.let {
            stringResource(R.string.ai_agent_task_cannot_move, it.uppercase())
        },
        refusal.currentStage.takeIf { it.isNotBlank() }?.let {
            stringResource(R.string.ai_agent_task_refusal_current, it)
        },
        // Only when it differs from what was asked for: when the backend
        // refuses the very stage it says may come next, the order is not
        // what is missing - the condition on the line below is.
        refusal.requiredNext.firstOrNull()?.takeIf { it != refusal.requestedStage }?.let {
            stringResource(R.string.ai_agent_task_refusal_required, it)
        },
        refusal.unmetCondition.takeIf { it.isNotBlank() }?.replaceFirstChar { it.uppercase() }?.plus(".")
    )

    lines.forEach { line ->
        Text(
            text = line,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}
