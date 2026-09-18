package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R

/**
 * One field: the plan the task will be executed by.
 *
 * Approving it is the backend's condition for leaving planning, so this
 * sends the text and nothing more - whether it may be approved at all
 * (the task has to be planning) is decided there.
 */
@Composable
fun ApprovePlanDialog(
    plan: String,
    isWorking: Boolean,
    onPlanChanged: (String) -> Unit,
    onApprove: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ai_agent_task_plan_dialog_title)) },
        text = {
            OutlinedTextField(
                value = plan,
                onValueChange = onPlanChanged,
                singleLine = false,
                placeholder = { Text(stringResource(R.string.ai_agent_task_plan_placeholder)) }
            )
        },
        confirmButton = {
            TextButton(onClick = onApprove, enabled = !isWorking && plan.isNotBlank()) {
                Text(stringResource(R.string.ai_agent_task_plan_approve))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ai_agent_task_plan_cancel))
            }
        }
    )
}
