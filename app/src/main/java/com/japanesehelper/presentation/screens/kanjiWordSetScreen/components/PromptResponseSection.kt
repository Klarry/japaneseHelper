package com.japanesehelper.presentation.screens.kanjiWordSetScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.japanesehelper.R
import com.japanesehelper.domain.model.KanjiWordSet
import com.japanesehelper.presentation.screens.homeScreen.components.CircularProgressIndicator
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.TabUiState

/**
 * Renders the currently selected experiment tab: a loading spinner, an error
 * with a retry action, or the PROMPT/RESPONSE blocks once the result has
 * loaded. [state.result.prompt][KanjiWordSet.prompt] is shown exactly as the
 * backend returned it - nothing here builds or alters a prompt, and no
 * intermediate model reasoning is ever surfaced.
 */
@Composable
fun PromptResponseSection(
    state: TabUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is TabUiState.Idle -> Unit

        is TabUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = LocalAppPadding.current.default),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is TabUiState.Error -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
            ) {
                Text(
                    text = state.message.ifEmpty { stringResource(R.string.kanji_word_set_generic_error) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = onRetry) {
                    Text(stringResource(R.string.kanji_word_set_retry))
                }
            }
        }

        is TabUiState.Success -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
            ) {
                LabeledBlock(caption = stringResource(R.string.kanji_word_set_prompt_title)) {
                    Text(text = state.result.prompt, style = MaterialTheme.typography.bodyMedium)
                }

                LabeledBlock(caption = stringResource(R.string.kanji_word_set_response_title)) {
                    ResponseDetails(state.result)
                }
            }
        }
    }
}

@Composable
private fun ResponseDetails(result: KanjiWordSet) {
    Column(verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.quarter)) {
        Text(
            text = stringResource(R.string.kanji_word_set_words_row, result.words.joinToString(", ")),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.kanji_word_set_cost_row, result.cost),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.kanji_word_set_value_row, result.value),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * One labelled block framed with dividers, matching the screen's PROMPT /
 * RESPONSE wireframe: an uppercase caption, then the content on a tinted
 * surface between a divider above and below - the same visual language as
 * [com.japanesehelper.presentation.screens.aiExplanationScreen.components.DescriptionSection].
 */
@Composable
private fun LabeledBlock(
    caption: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
    ) {
        Text(
            text = caption.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Box(modifier = Modifier.padding(LocalAppPadding.current.default)) {
                content()
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
