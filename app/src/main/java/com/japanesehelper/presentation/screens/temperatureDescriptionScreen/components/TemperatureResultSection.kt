package com.japanesehelper.presentation.screens.temperatureDescriptionScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.japanesehelper.R
import com.japanesehelper.domain.model.TemperatureDescription
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.TintedSurface
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.TemperatureResultUiState

private val RUN_BUTTON_SPINNER_SIZE = 16.dp
private const val RUN_BUTTON_SPINNER_STROKE_WIDTH = 2

/**
 * One independent temperature section: a "Run experiment" button (an inline
 * spinner replaces its label while loading, so nothing shifts vertically),
 * an error with retry, or the generated sentence and its translation once
 * done. Running one temperature never affects the others - each keeps its
 * own [TemperatureResultUiState].
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
            text = stringResource(R.string.temperature_description_section_title, temperature.toLabel()),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        when (resultState) {
            is TemperatureResultUiState.Idle -> RunButton(isLoading = false, onRun = onRun)
            is TemperatureResultUiState.Loading -> RunButton(isLoading = true, onRun = onRun)
            is TemperatureResultUiState.Error -> ErrorWithRetry(message = resultState.message, onRetry = onRun)
            is TemperatureResultUiState.Success -> ResultDetails(resultState.result)
        }
    }
}

/** The run action for one section: label while idle, inline spinner while loading - never both states at once. */
@Composable
private fun RunButton(isLoading: Boolean, onRun: () -> Unit) {
    OutlinedButton(
        onClick = onRun,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(RUN_BUTTON_SPINNER_SIZE),
                strokeWidth = RUN_BUTTON_SPINNER_STROKE_WIDTH.dp
            )
            Spacer(Modifier.width(LocalAppPadding.current.half))
        }
        Text(stringResource(R.string.temperature_description_run_button))
    }
}

@Composable
private fun ResultDetails(result: TemperatureDescription) {
    TintedSurface {
        Column(verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.quarter)) {
            Text(text = result.sentence, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = result.translation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** "0.0" -> "0", "0.7" -> "0.7" - matches how the temperatures are written in the spec. */
private fun Double.toLabel(): String = if (this % 1.0 == 0.0) toInt().toString() else toString()
