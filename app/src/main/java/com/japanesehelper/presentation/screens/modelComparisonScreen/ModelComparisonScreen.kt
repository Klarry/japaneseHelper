package com.japanesehelper.presentation.screens.modelComparisonScreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.japanesehelper.R
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenScaffold
import com.japanesehelper.presentation.screens.homeScreen.components.StyledWord
import com.japanesehelper.presentation.screens.modelComparisonScreen.components.ModelComparisonPromptCard
import com.japanesehelper.presentation.screens.modelComparisonScreen.components.ModelResultsSection
import com.japanesehelper.presentation.screens.modelComparisonScreen.components.RunComparisonButton
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.viewmodel.ModelComparisonViewModel
import com.japanesehelper.presentation.viewmodel.screendata.ModelComparisonResultUiState

/**
 * Day 5: Model Comparison. One kanji, one prompt, sent once to the backend
 * with all of COMPARISON_MODELS - nothing is requested until the user taps
 * "Run comparison".
 */
@Composable
fun ModelComparisonScreen(
    navController: NavController,
    viewModel: ModelComparisonViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ScreenScaffold(
        title = stringResource(R.string.model_comparison_title),
        onBack = { navController.popBackStack() }
    ) {
        Text(
            text = stringResource(R.string.model_comparison_description),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Text(
            text = state.kanji,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = TextStyle(fontSize = LocalAppFontSize.current.font70),
            textAlign = TextAlign.Center
        )

        StyledWord(word = state.furigana, caption = stringResource(R.string.home_furigana_caption))
        StyledWord(word = state.meaning, caption = stringResource(R.string.home_meaning_caption))

        ModelComparisonPromptCard(kanji = state.kanji)

        when (val result = state.result) {
            is ModelComparisonResultUiState.Idle ->
                RunComparisonButton(isLoading = false, onRun = viewModel::run)

            is ModelComparisonResultUiState.Loading ->
                RunComparisonButton(isLoading = true, onRun = viewModel::run)

            is ModelComparisonResultUiState.Error ->
                ErrorWithRetry(message = result.message, onRetry = viewModel::run)

            is ModelComparisonResultUiState.Success -> Unit
        }

        ModelResultsSection(resultState = state.result)
    }
}
