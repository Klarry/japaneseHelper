package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * The branch the conversation is on, the branches it can move to, and the two
 * actions that make more of them. Everything shown comes from the backend's
 * context: the device holds no branches of its own.
 */
@Composable
fun BranchingControls(
    context: AgentContext?,
    isWorking: Boolean,
    onSwitchBranch: (String) -> Unit,
    onCreateCheckpoint: () -> Unit,
    onCreateBranch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val padding = LocalAppPadding.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = padding.default),
        verticalArrangement = Arrangement.spacedBy(padding.quarter)
    ) {
        ReadoutCaption(stringResource(R.string.ai_agent_branch_title))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(padding.half)
        ) {
            context?.branches.orEmpty().forEach { branch ->
                FilterChip(
                    selected = branch == context?.branch,
                    onClick = { onSwitchBranch(branch) },
                    enabled = !isWorking,
                    label = { Text(branch) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(padding.half)) {
            OutlinedButton(onClick = onCreateCheckpoint, enabled = !isWorking) {
                Text(stringResource(R.string.ai_agent_create_checkpoint_button))
            }
            OutlinedButton(
                onClick = onCreateBranch,
                enabled = !isWorking && !context?.checkpoints.isNullOrEmpty()
            ) {
                Text(stringResource(R.string.ai_agent_create_branch_button))
            }
        }
    }
}
