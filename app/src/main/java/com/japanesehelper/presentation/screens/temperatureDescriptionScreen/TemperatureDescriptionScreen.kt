package com.japanesehelper.presentation.screens.temperatureDescriptionScreen

import androidx.compose.foundation.layout.Arrangement
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
import com.japanesehelper.domain.model.SUPPORTED_TEMPERATURES
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenTopBar
import com.japanesehelper.presentation.screens.homeScreen.components.StyledWord
import com.japanesehelper.presentation.screens.temperatureDescriptionScreen.components.PromptCard
import com.japanesehelper.presentation.screens.temperatureDescriptionScreen.components.TemperatureResultSection
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.TemperatureDescriptionViewModel
import com.japanesehelper.presentation.viewmodel.screendata.TemperatureResultUiState

/**
 * Temperature Description screen: shows the dynamic kanji the screen was
 * opened for - the visual focus, sized the same as Kanji Word Set's - the
 * single prompt template used for every request, and one independent
 * section per supported temperature (0, 0.7, 1.2) so the user can compare
 * how the same prompt behaves as temperature changes.
 *
 * Nothing is requested automatically - each section only runs once its own
 * "Run experiment" button is tapped (see [TemperatureDescriptionViewModel]).
 *
 * @param navController Used only to pop back to the previous screen.
 * @param viewModel Scoped to this screen's nav back stack entry; the kanji,
 * furigana and meaning all come from the nav argument, never hardcoded.
 */
@Composable
fun TemperatureDescriptionScreen(
    navController: NavController,
    viewModel: TemperatureDescriptionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScreenTopBar(
                title = stringResource(R.string.temperature_description_title),
                onBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(LocalAppPadding.current.default),
            verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
        ) {
            Text(
                text = state.kanji,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontSize = LocalAppFontSize.current.font70),
                textAlign = TextAlign.Center
            )

            StyledWord(word = state.furigana, caption = stringResource(R.string.home_furigana_caption))
            StyledWord(word = state.meaning, caption = stringResource(R.string.home_meaning_caption))

            PromptCard(kanji = state.kanji)

            SUPPORTED_TEMPERATURES.forEach { temperature ->
                TemperatureResultSection(
                    temperature = temperature,
                    resultState = state.results[temperature] ?: TemperatureResultUiState.Idle,
                    onRun = { viewModel.run(temperature) }
                )
            }
        }
    }
}
