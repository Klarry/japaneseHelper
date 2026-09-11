package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.NewBranchState

/**
 * Names the branch and picks the checkpoint to fork it from. Two branches
 * made from the same checkpoint are exactly the interesting case, so the
 * checkpoint stays selectable rather than always being the newest one.
 */
@Composable
fun CreateBranchDialog(
    state: NewBranchState,
    checkpoints: List<String>,
    onNameChanged: (String) -> Unit,
    onCheckpointSelected: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val padding = LocalAppPadding.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ai_agent_branch_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(padding.half)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.ai_agent_branch_name_placeholder)) },
                    singleLine = true
                )

                Text(
                    text = stringResource(R.string.ai_agent_branch_from_checkpoint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(padding.half)
                ) {
                    checkpoints.forEach { checkpoint ->
                        FilterChip(
                            selected = checkpoint == state.checkpoint,
                            onClick = { onCheckpointSelected(checkpoint) },
                            label = { Text(checkpoint) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = state.name.isNotBlank() && state.checkpoint.isNotBlank()
            ) {
                Text(stringResource(R.string.ai_agent_branch_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ai_agent_branch_cancel))
            }
        }
    )
}
