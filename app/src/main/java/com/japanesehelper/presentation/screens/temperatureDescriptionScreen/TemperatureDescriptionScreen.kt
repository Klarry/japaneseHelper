package com.japanesehelper.presentation.screens.temperatureDescriptionScreen

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
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenScaffold
import com.japanesehelper.presentation.screens.homeScreen.components.StyledWord
import com.japanesehelper.presentation.screens.temperatureDescriptionScreen.components.PromptCard
import com.japanesehelper.presentation.screens.temperatureDescriptionScreen.components.TemperatureResultSection
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.viewmodel.TemperatureDescriptionViewModel
import com.japanesehelper.presentation.viewmodel.screendata.TemperatureResultUiState

@Composable
fun TemperatureDescriptionScreen(
    navController: NavController,
    viewModel: TemperatureDescriptionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ScreenScaffold(
        title = stringResource(R.string.temperature_description_title),
        onBack = { navController.popBackStack() }
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
