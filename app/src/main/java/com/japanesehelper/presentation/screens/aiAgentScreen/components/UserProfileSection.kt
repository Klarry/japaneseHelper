package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentProfilePreset
import com.japanesehelper.domain.model.AgentUserProfile
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * How the learner wants to be answered, and the two profiles the comparison
 * is made with. Selecting a profile sends it to the backend, which applies
 * it to every request from then on - the device never touches the prompt.
 */
@Composable
fun UserProfileSection(
    profile: AgentUserProfile?,
    isWorking: Boolean,
    onPresetSelected: (AgentProfilePreset) -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val padding = LocalAppPadding.current
    val selected = AgentProfilePreset.matching(profile)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReadoutCaption(stringResource(R.string.ai_agent_profile_title))

            TextButton(onClick = onEdit, enabled = !isWorking) {
                Text(
                    text = stringResource(R.string.ai_agent_profile_edit_button),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Text(
            text = profile.asSummary(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(horizontalArrangement = Arrangement.spacedBy(padding.half)) {
            AgentProfilePreset.entries.forEach { preset ->
                FilterChip(
                    selected = preset == selected,
                    onClick = { onPresetSelected(preset) },
                    enabled = !isWorking,
                    label = { Text(preset.label) }
                )
            }
        }
    }
}

/** The four settings on one line, in the order the dialog edits them. */
@Composable
private fun AgentUserProfile?.asSummary(): String {
    val values = listOfNotNull(
        this?.japaneseLevel,
        this?.explanationStyle,
        this?.answerFormat,
        this?.translationLanguage
    ).filter { it.isNotBlank() }

    return values.joinToString(" · ").ifEmpty { stringResource(R.string.ai_agent_profile_empty) }
}
