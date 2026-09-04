package com.japanesehelper.presentation.screens.modelComparisonScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.japanesehelper.R
import com.japanesehelper.domain.model.ModelComparisonEntry
import com.japanesehelper.presentation.screens.homeScreen.components.CenteredLoadingIndicator
import com.japanesehelper.presentation.screens.homeScreen.components.TintedSurface
import com.japanesehelper.presentation.theme.LocalAppPadding

private const val NO_TOKEN_COUNT = "—"

/** What one model's card shows before/during/after the single comparison run. */
sealed class ModelCardPhase {
    data object NotRun : ModelCardPhase()
    data object Running : ModelCardPhase()
    data class Result(val entry: ModelComparisonEntry) : ModelCardPhase()
}

@Composable
fun ModelResultCard(
    label: String,
    modelId: String,
    phase: ModelCardPhase,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
    ) {
        Text(
            text = stringResource(R.string.model_comparison_card_title, label, modelId),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        when (phase) {
            is ModelCardPhase.NotRun -> Text(
                text = stringResource(R.string.model_comparison_not_run_yet),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            is ModelCardPhase.Running -> CenteredLoadingIndicator()

            is ModelCardPhase.Result -> ResultBody(phase.entry)
        }
    }
}

@Composable
private fun ResultBody(entry: ModelComparisonEntry) {
    val error = entry.error
    if (error != null) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        return
    }

    TintedSurface {
        Column(verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.quarter)) {
            Text(text = entry.text.orEmpty(), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(R.string.model_comparison_time_row, entry.responseTimeMs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(
                    R.string.model_comparison_input_tokens_row,
                    entry.inputTokens?.toString() ?: NO_TOKEN_COUNT
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(
                    R.string.model_comparison_output_tokens_row,
                    entry.outputTokens?.toString() ?: NO_TOKEN_COUNT
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
