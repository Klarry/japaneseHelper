package com.japanesehelper.presentation.screens.kanjiWordSetScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.KanjiWordSet
import com.japanesehelper.presentation.screens.homeScreen.components.CenteredLoadingIndicator
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.LabeledBlock
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

        is TabUiState.Loading -> CenteredLoadingIndicator(modifier)

        is TabUiState.Error -> ErrorWithRetry(message = state.message, onRetry = onRetry, modifier = modifier)

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
