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
fun KanjiWordSetButton(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsState()
    val word = state.randomWord?.word

    HomeActionButton(
        text = stringResource(R.string.home_kanji_word_set_button),
        enabled = word != null,
        onClick = { word?.let { navController.navigate(Screens.KanjiWordSet.createRoute(it)) } },
        modifier = modifier
    )
}
