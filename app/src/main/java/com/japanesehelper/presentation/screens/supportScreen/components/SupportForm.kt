package com.japanesehelper.presentation.screens.supportScreen.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.japanesehelper.R
import com.japanesehelper.domain.model.ModelProvider
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.DEMO_TICKET_IDS

private const val QUESTION_MIN_LINES = 2

/** The ticket the question is about. Any id can be typed - the backend says
 * whether it exists. */
@Composable
fun TicketIdField(
    ticketId: String,
    showError: Boolean,
    onChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = ticketId,
        onValueChange = onChanged,
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.support_ticket_label)) },
        supportingText = {
            Text(
                stringResource(
                    if (showError) R.string.support_ticket_invalid else R.string.support_ticket_hint
                )
            )
        },
        isError = showError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
    )
}

/** Shortcuts to the backend's synthetic tickets. Choosing one only fills the
 * field; nothing about the ticket is shown until the backend has read it. */
@Composable
fun DemoTicketChips(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.support_demo_tickets),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        DEMO_TICKET_IDS.forEach { id ->
            FilterChip(
                selected = selected == id,
                onClick = { onSelected(id) },
                label = { Text(id) }
            )
        }
    }
}

/** Which model answers. The same two names as the AI Agent screen; the
 * backend never swaps one for the other. */
@Composable
fun SupportProviderChips(
    provider: ModelProvider,
    onChanged: (ModelProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.ai_agent_provider_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ModelProvider.entries.forEach { option ->
            FilterChip(
                selected = provider == option,
                onClick = { onChanged(option) },
                label = { Text(stringResource(option.labelRes())) }
            )
        }
    }
}

@Composable
fun QuestionField(
    question: String,
    onChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = question,
        onValueChange = onChanged,
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.support_question_label)) },
        placeholder = { Text(stringResource(R.string.support_question_placeholder)) },
        minLines = QUESTION_MIN_LINES
    )
}

private fun ModelProvider.labelRes(): Int = when (this) {
    ModelProvider.CLOUD -> R.string.ai_agent_provider_cloud
    ModelProvider.LOCAL -> R.string.ai_agent_provider_local
}
