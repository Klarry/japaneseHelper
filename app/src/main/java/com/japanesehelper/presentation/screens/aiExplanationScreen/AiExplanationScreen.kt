package com.japanesehelper.presentation.screens.aiExplanationScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.AiExplanationViewModel

/**
 * AI Explanation screen: requests and shows Gemini's uncontrolled and
 * constrained explanations of the current word's meaning. Loading starts
 * only once this screen opens (see [AiExplanationViewModel]) - never
 * automatically on Home.
 *
 * @param navController Used only to pop back to the previous screen.
 * @param viewModel Scoped to this screen's nav back stack entry; the word
 * and meaning come from the nav argument, never hardcoded.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiExplanationScreen(
    navController: NavController,
    viewModel: AiExplanationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ai_explanation_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(LocalAppPadding.current.default)
        ) {
            Text(
                text = viewModel.word,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = LocalAppPadding.current.default),
                style = TextStyle(fontSize = LocalAppFontSize.current.font40),
                textAlign = TextAlign.Center
            )

            DescriptionSection(
                state = state,
                onRetry = viewModel::retry
            )
        }
    }
}
