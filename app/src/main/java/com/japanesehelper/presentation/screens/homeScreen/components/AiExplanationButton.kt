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
 * Entry point into the AI Explanation screen.
 *
 * Always visible - it does not wait for the random word to load - but only
 * enabled once both a word and its meaning are available, since the screen
 * needs both dynamically (never hardcoded) to request an explanation.
 *
 * @param navController Used to navigate to [Screens.AiExplanation].
 * @param modifier Optional [Modifier] for styling and layout adjustments.
 * @param viewModel The [VocabViewModel] instance providing the current random word.
 */
@Composable
fun AiExplanationButton(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsState()
    val word = state.randomWord?.word
    val meaning = state.randomWord?.meaning

    OutlinedButton(
        onClick = {
            if (!word.isNullOrEmpty() && !meaning.isNullOrEmpty()) {
                navController.navigate(Screens.AiExplanation.createRoute(word, meaning))
            }
        },
        enabled = !word.isNullOrEmpty() && !meaning.isNullOrEmpty(),
        modifier = modifier
    ) {
        Text(stringResource(R.string.home_ai_explanation_button))
    }
}
