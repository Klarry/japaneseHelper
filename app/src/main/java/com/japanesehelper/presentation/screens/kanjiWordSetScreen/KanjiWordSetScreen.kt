package com.japanesehelper.presentation.screens.kanjiWordSetScreen

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
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenScaffold
import com.japanesehelper.presentation.screens.kanjiWordSetScreen.components.ExperimentTabRow
import com.japanesehelper.presentation.screens.kanjiWordSetScreen.components.PromptResponseSection
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.viewmodel.KanjiWordSetViewModel
import com.japanesehelper.presentation.viewmodel.screendata.TabUiState

@Composable
fun KanjiWordSetScreen(
    navController: NavController,
    viewModel: KanjiWordSetViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ScreenScaffold(
        title = stringResource(R.string.kanji_word_set_title),
        onBack = { navController.popBackStack() }
    ) {
        Text(
            text = state.kanji,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = TextStyle(fontSize = LocalAppFontSize.current.font70),
            textAlign = TextAlign.Center
        )

        ExperimentTabRow(
            selectedTab = state.selectedTab,
            onTabSelected = viewModel::selectTab
        )

        PromptResponseSection(
            state = state.tabs[state.selectedTab] ?: TabUiState.Idle,
            onRetry = { viewModel.retry(state.selectedTab) }
        )
    }
}
