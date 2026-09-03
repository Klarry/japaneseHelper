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

/**
 * The single prompt template used for all three temperature requests, with
 * the current kanji already substituted in. Deliberately styled smaller and
 * more muted than the kanji/results below it - it's context, not the focus.
 * Purely informational - nothing here is sent to the backend, which applies
 * the same template server-side; only the temperature differs per request.
 *
 * @param kanji The dynamic kanji/word shown inside the template text.
 */
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
