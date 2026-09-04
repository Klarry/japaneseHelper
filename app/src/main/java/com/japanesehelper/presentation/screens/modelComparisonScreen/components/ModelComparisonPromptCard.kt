package com.japanesehelper.presentation.screens.modelComparisonScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.presentation.screens.homeScreen.components.TintedSurface
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Display-only preview of the prompt sent to the backend. The literal request
 * text (with the `{KANJI}` placeholder) lives in MODEL_COMPARISON_PROMPT and
 * is identical for every compared model - only the model id varies.
 */
@Composable
fun ModelComparisonPromptCard(kanji: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
    ) {
        TintedSurface {
            Text(
                text = stringResource(R.string.model_comparison_prompt_template, kanji),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = stringResource(R.string.model_comparison_prompt_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
