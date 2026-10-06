package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.LocalLlmHealth
import com.japanesehelper.domain.model.ModelProvider
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Day 27: which model answers the next message, and whether it can.
 *
 * Two chips and one line of status. The line matters as much as the chips:
 * a local model that is not running fails loudly here rather than silently
 * becoming a cloud answer, so the person can see *before* asking whether
 * the switch means what it says.
 *
 * The device decides nothing about the model. It names a mode, and reads
 * back what the backend reports about it.
 */
@Composable
fun ProviderSwitch(
    provider: ModelProvider,
    health: LocalLlmHealth?,
    isChecking: Boolean,
    onChanged: (ModelProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    val padding = LocalAppPadding.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(padding.quarter)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(padding.half),
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

        // Only while the local model is chosen: a status line about
        // something nobody is using is noise, and it would go stale.
        if (provider.isLocal) {
            Text(
                text = statusLine(health, isChecking),
                style = MaterialTheme.typography.labelSmall,
                color = if (health?.isReady == true) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

/**
 * What the status line says, in the backend's terms.
 *
 * "Connected" is claimed only when the server answered *and* has the model:
 * a running Ollama without the weights pulled cannot answer a question, and
 * telling the person it is connected would be a lie they discover one
 * message later.
 */
@Composable
private fun statusLine(health: LocalLlmHealth?, isChecking: Boolean): String = when {
    isChecking && health == null -> stringResource(R.string.ai_agent_local_llm_checking)
    health == null -> stringResource(R.string.ai_agent_local_llm_unknown)
    health.isReady -> stringResource(R.string.ai_agent_local_llm_connected, health.model)
    health.available -> stringResource(R.string.ai_agent_local_llm_not_pulled, health.model)
    else -> stringResource(R.string.ai_agent_local_llm_unavailable)
}

/** The chip's caption. Kept next to the switch rather than on the enum: the
 * enum says which model answers, and has no business knowing about
 * resources. */
private fun ModelProvider.labelRes(): Int = when (this) {
    ModelProvider.CLOUD -> R.string.ai_agent_provider_cloud
    ModelProvider.LOCAL -> R.string.ai_agent_provider_local
}
