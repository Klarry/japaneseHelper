package com.japanesehelper.presentation.screens.homeScreen.components

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

@Composable
fun ModelComparisonButton(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsState()
    val word = state.randomWord?.word
    val furigana = state.randomWord?.furigana.orEmpty()
    val meaning = state.randomWord?.meaning.orEmpty()

    HomeActionButton(
        text = stringResource(R.string.home_model_comparison_button),
        enabled = word != null,
        onClick = {
            word?.let {
                navController.navigate(Screens.ModelComparison.createRoute(it, furigana, meaning))
            }
        },
        modifier = modifier
    )
}
