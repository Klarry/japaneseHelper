package com.japanesehelper.presentation.screens.kanjiWordSetScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
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
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenTopBar
import com.japanesehelper.presentation.screens.kanjiWordSetScreen.components.ExperimentTabRow
import com.japanesehelper.presentation.screens.kanjiWordSetScreen.components.PromptResponseSection
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.KanjiWordSetViewModel
import com.japanesehelper.presentation.viewmodel.screendata.TabUiState

/**
 * Kanji Word Set screen: shows the dynamic kanji the screen was opened for
 * and lets the user compare its four prompting experiments side by side via
 * tabs. All four are requested concurrently as soon as the screen opens (see
 * [KanjiWordSetViewModel]), so switching tabs is instant and never waits on
 * a response.
 *
 * @param navController Used only to pop back to the previous screen.
 * @param viewModel Scoped to this screen's nav back stack entry; the kanji
 * comes from the nav argument, never hardcoded.
 */
@Composable
fun KanjiWordSetScreen(
    navController: NavController,
    viewModel: KanjiWordSetViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScreenTopBar(
                title = stringResource(R.string.kanji_word_set_title),
                onBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = state.kanji,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = LocalAppPadding.current.default),
                style = TextStyle(fontSize = LocalAppFontSize.current.font70),
                textAlign = TextAlign.Center
            )

            ExperimentTabRow(
                selectedTab = state.selectedTab,
                onTabSelected = viewModel::selectTab,
                modifier = Modifier.padding(top = LocalAppPadding.current.default)
            )

            PromptResponseSection(
                state = state.tabs[state.selectedTab] ?: TabUiState.Idle,
                onRetry = { viewModel.retry(state.selectedTab) },
                modifier = Modifier.padding(LocalAppPadding.current.default)
            )
        }
    }
}
