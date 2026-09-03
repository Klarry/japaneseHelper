package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.japanesehelper.R
import com.japanesehelper.presentation.navigation.Screens
import com.japanesehelper.presentation.viewmodel.VocabViewModel

/**
 * Entry point into the Temperature Description screen.
 *
 * Always visible - it does not wait for the random word to load - but only
 * enabled once a word is available, since the screen needs a dynamic kanji
 * to open with (never hardcoded). Matches [KanjiWordSetButton] and
 * [AiExplanationButton]: the whole word is passed on, not just its first
 * character.
 *
 * @param navController Used to navigate to [Screens.TemperatureDescription].
 * @param modifier Optional [Modifier] for styling and layout adjustments.
 * @param viewModel The [VocabViewModel] instance providing the current random word.
 */
@Composable
fun TemperatureDescriptionButton(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsState()
    val word = state.randomWord?.word
    val furigana = state.randomWord?.furigana.orEmpty()
    val meaning = state.randomWord?.meaning.orEmpty()

    OutlinedButton(
        onClick = {
            word?.let {
                navController.navigate(Screens.TemperatureDescription.createRoute(it, furigana, meaning))
            }
        },
        enabled = word != null,
        modifier = modifier
    ) {
        Text(stringResource(R.string.home_temperature_description_button))
    }
}
