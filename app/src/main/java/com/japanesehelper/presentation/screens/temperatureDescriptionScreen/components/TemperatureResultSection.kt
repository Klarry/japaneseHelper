package com.japanesehelper.presentation.screens.temperatureDescriptionScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.japanesehelper.R
import com.japanesehelper.domain.model.TemperatureDescription
import com.japanesehelper.presentation.screens.homeScreen.components.CircularProgressIndicator
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.TemperatureResultUiState

/**
 * One independent temperature section: a "Run experiment" button, a loading
 * spinner (button disabled while in flight), an error with retry, or the
 * generated sentence and its translation once done. Running one temperature
 * never affects the others - each keeps its own [TemperatureResultUiState].
 *
 * @param temperature The sampling temperature this section runs at.
 * @param resultState This section's current, independent state.
 * @param onRun Called to start (or retry) this section's request.
 */
@Composable
fun TemperatureResultSection(
    temperature: Double,
    resultState: TemperatureResultUiState,
    onRun: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
    ) {
        Text(
            text = stringResource(
                R.string.temperature_description_section_title,
                temperature.toLabel()
            ),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        when (resultState) {
            is TemperatureResultUiState.Idle -> {
                OutlinedButton(onClick = onRun) {
                    Text(stringResource(R.string.temperature_description_run_button))
                }
            }

            is TemperatureResultUiState.Loading -> {
                Column(verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)) {
                    OutlinedButton(onClick = onRun, enabled = false) {
                        Text(stringResource(R.string.temperature_description_run_button))
                    }
                    CircularProgressIndicator()
                }
            }

            is TemperatureResultUiState.Error -> {
                Column(verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)) {
                    Text(
                        text = resultState.message.ifEmpty {
                            stringResource(R.string.temperature_description_generic_error)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = onRun) {
                        Text(stringResource(R.string.temperature_description_retry))
                    }
                }
            }

            is TemperatureResultUiState.Success -> {
                ResultDetails(resultState.result)
            }
        }
    }
}

@Composable
private fun ResultDetails(result: TemperatureDescription) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(LocalAppPadding.current.default),
            verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.quarter)
        ) {
            Text(text = result.sentence, style = MaterialTheme.typography.bodyLarge)
            Text(text = result.translation, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** "0.0" -> "0", "0.7" -> "0.7" - matches how the temperatures are written in the spec. */
private fun Double.toLabel(): String = if (this % 1.0 == 0.0) toInt().toString() else toString()
