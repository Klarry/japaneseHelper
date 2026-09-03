package com.japanesehelper.presentation.screens.temperatureDescriptionScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.japanesehelper.R
import com.japanesehelper.domain.model.TemperatureDescription
import com.japanesehelper.presentation.screens.homeScreen.components.AppProgressIndicator
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.TintedSurface
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.theme.LocalAppSize
import com.japanesehelper.presentation.viewmodel.screendata.TemperatureResultUiState

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

@Composable
private fun RunButton(isLoading: Boolean, onRun: () -> Unit) {
    OutlinedButton(
        onClick = onRun,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            AppProgressIndicator(size = LocalAppSize.current.indicatorSmall)
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

private fun Double.toLabel(): String = if (this % 1.0 == 0.0) toInt().toString() else toString()
