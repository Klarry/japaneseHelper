package com.japanesehelper.presentation.screens.temperatureDescriptionScreen.components

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

/** Display-only: the backend builds the real prompt, only the temperature varies per request. */
@Composable
fun PromptCard(kanji: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
    ) {
        TintedSurface {
            Text(
                text = stringResource(R.string.temperature_description_prompt_template, kanji),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = stringResource(R.string.temperature_description_prompt_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
