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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentProfileOptions
import com.japanesehelper.domain.model.AgentUserProfile
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * The four settings, one row of chips each - same chip style the branch
 * dialog uses. Free text would let a typo become a setting the backend
 * dutifully puts in every prompt.
 */
@Composable
fun EditProfileDialog(
    profile: AgentUserProfile,
    onProfileChanged: (AgentUserProfile) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val padding = LocalAppPadding.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ai_agent_profile_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(padding.half)) {
                SettingRow(
                    label = stringResource(R.string.ai_agent_profile_level),
                    options = AgentProfileOptions.JAPANESE_LEVELS,
                    selected = profile.japaneseLevel,
                    onSelected = { onProfileChanged(profile.copy(japaneseLevel = it)) }
                )
                SettingRow(
                    label = stringResource(R.string.ai_agent_profile_style),
                    options = AgentProfileOptions.EXPLANATION_STYLES,
                    selected = profile.explanationStyle,
                    onSelected = { onProfileChanged(profile.copy(explanationStyle = it)) }
                )
                SettingRow(
                    label = stringResource(R.string.ai_agent_profile_format),
                    options = AgentProfileOptions.ANSWER_FORMATS,
                    selected = profile.answerFormat,
                    onSelected = { onProfileChanged(profile.copy(answerFormat = it)) }
                )
                SettingRow(
                    label = stringResource(R.string.ai_agent_profile_translation),
                    options = AgentProfileOptions.TRANSLATION_LANGUAGES,
                    selected = profile.translationLanguage,
                    onSelected = { onProfileChanged(profile.copy(translationLanguage = it)) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.ai_agent_profile_save))
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
private fun SettingRow(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    val padding = LocalAppPadding.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(padding.half)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelected(option) },
                    label = { Text(option) }
                )
            }
        }
    }
}
