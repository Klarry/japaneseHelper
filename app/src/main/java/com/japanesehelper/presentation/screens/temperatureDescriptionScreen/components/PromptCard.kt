package com.japanesehelper.presentation.screens.temperatureDescriptionScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * The single prompt template used for all three temperature requests, with
 * the current kanji already substituted in. Purely informational - nothing
 * here is sent to the backend, which applies the same template server-side;
 * only the temperature differs between the three requests.
 *
 * @param kanji The dynamic kanji/word shown inside the template text.
 */
@Composable
fun PromptCard(kanji: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Text(
                text = stringResource(R.string.temperature_description_prompt_template, kanji),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(LocalAppPadding.current.default)
            )
        }

        Text(
            text = stringResource(R.string.temperature_description_prompt_label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
