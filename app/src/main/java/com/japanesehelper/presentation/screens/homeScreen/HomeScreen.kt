package com.japanesehelper.presentation.screens.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.japanesehelper.presentation.screens.homeScreen.components.AiExplanationButton
import com.japanesehelper.presentation.screens.homeScreen.components.Header
import com.japanesehelper.presentation.screens.homeScreen.components.KanjiWordSetButton
import com.japanesehelper.presentation.screens.homeScreen.components.LevelDropdownMenu
import com.japanesehelper.presentation.screens.homeScreen.components.ModelComparisonButton
import com.japanesehelper.presentation.screens.homeScreen.components.RandomWordCard
import com.japanesehelper.presentation.screens.homeScreen.components.TemperatureDescriptionButton
import com.japanesehelper.presentation.theme.LocalAppPadding

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(LocalAppPadding.current.default),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
        ) {
            Header()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
            ) {
                KanjiWordSetButton(navController = navController, modifier = Modifier.weight(1f))
                AiExplanationButton(navController = navController, modifier = Modifier.weight(1f))
                TemperatureDescriptionButton(navController = navController, modifier = Modifier.weight(1f))
                ModelComparisonButton(navController = navController, modifier = Modifier.weight(1f))
            }

            LevelDropdownMenu()
            RandomWordCard()
        }
    }
}
