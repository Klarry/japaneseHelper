package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.japanesehelper.domain.model.AgentInvariant
import com.japanesehelper.domain.model.AgentInvariantCategory
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.InvariantEditorState

/** Enough room for a few rules without pushing the editor off the screen. */
private val RULES_MAX_HEIGHT = 190.dp

/**
 * The rules, and one row for writing them. Tap a rule to change it, "x" to
 * remove it, or type a new one and add it - all of which the backend
 * carries out. The device sends the change and displays what comes back.
 */
@Composable
fun EditInvariantsDialog(
    invariants: List<AgentInvariant>,
    editor: InvariantEditorState,
    isWorking: Boolean,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit,
    onRuleChanged: (String) -> Unit,
    onCategorySelected: (AgentInvariantCategory) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val padding = LocalAppPadding.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ai_agent_invariants_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(padding.half)) {
                Column(
                    modifier = Modifier
                        .heightIn(max = RULES_MAX_HEIGHT)
                        .verticalScroll(rememberScrollState())
                ) {
                    invariants.forEach { invariant ->
                        InvariantRow(
                            invariant = invariant,
                            isSelected = invariant.id == editor.editingId,
                            isWorking = isWorking,
                            onSelect = onSelect,
                            onDelete = onDelete
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(padding.half)
                ) {
                    AgentInvariantCategory.entries.forEach { category ->
                        FilterChip(
                            selected = category == editor.category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(category.label) }
                        )
                    }
                }

                OutlinedTextField(
                    value = editor.rule,
                    onValueChange = onRuleChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.ai_agent_invariants_rule_placeholder)) },
                    textStyle = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !isWorking && editor.rule.isNotBlank()) {
                Text(
                    stringResource(
                        if (editor.editingId == null) R.string.ai_agent_invariants_add
                        else R.string.ai_agent_invariants_save
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ai_agent_branch_cancel))
            }
        }
    )
}

@Composable
private fun InvariantRow(
    invariant: AgentInvariant,
    isSelected: Boolean,
    isWorking: Boolean,
    onSelect: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isWorking) { onSelect(invariant.id) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = invariant.rule,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        TextButton(onClick = { onDelete(invariant.id) }, enabled = !isWorking) {
            Text(
                text = stringResource(R.string.ai_agent_invariants_remove),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
