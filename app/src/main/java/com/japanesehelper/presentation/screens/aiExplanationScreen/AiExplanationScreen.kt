package com.japanesehelper.presentation.screens.aiExplanationScreen

import androidx.compose.foundation.layout.padding
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
import com.japanesehelper.presentation.screens.aiExplanationScreen.components.DescriptionSection
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenScaffold
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.AiExplanationViewModel

@Composable
fun AiExplanationScreen(
    navController: NavController,
    viewModel: AiExplanationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ScreenScaffold(
        title = stringResource(R.string.ai_explanation_title),
        onBack = { navController.popBackStack() }
    ) {
        Text(
            text = viewModel.word,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = LocalAppPadding.current.default),
            style = TextStyle(fontSize = LocalAppFontSize.current.font40),
            textAlign = TextAlign.Center
        )

        DescriptionSection(state = state, onRetry = viewModel::retry)
    }
}
