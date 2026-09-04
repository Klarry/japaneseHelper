package com.japanesehelper.presentation.screens.modelComparisonScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.COMPARISON_MODELS
import com.japanesehelper.domain.model.ComparisonStrength
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.ModelComparisonResultUiState

@Composable
fun ModelResultsSection(
    resultState: ModelComparisonResultUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
    ) {
        COMPARISON_MODELS.forEach { model ->
            val entry = (resultState as? ModelComparisonResultUiState.Success)
                ?.comparison
                ?.results
                ?.firstOrNull { it.model == model.modelId }

            val phase = when {
                entry != null -> ModelCardPhase.Result(entry)
                resultState is ModelComparisonResultUiState.Loading -> ModelCardPhase.Running
                else -> ModelCardPhase.NotRun
            }

            ModelResultCard(
                label = stringResource(model.strength.labelRes()),
                modelId = model.modelId,
                phase = phase
            )
        }
    }
}

private fun ComparisonStrength.labelRes(): Int = when (this) {
    ComparisonStrength.LIGHT -> R.string.model_comparison_strength_light
    ComparisonStrength.STANDARD -> R.string.model_comparison_strength_standard
    ComparisonStrength.PRO -> R.string.model_comparison_strength_pro
}
