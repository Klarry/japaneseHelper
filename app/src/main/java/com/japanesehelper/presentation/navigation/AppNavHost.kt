package com.japanesehelper.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.japanesehelper.presentation.screens.aiExplanationScreen.AiExplanationScreen
import com.japanesehelper.presentation.screens.homeScreen.HomeScreen
import com.japanesehelper.presentation.screens.kanjiWordSetScreen.KanjiWordSetScreen
import com.japanesehelper.presentation.screens.temperatureDescriptionScreen.TemperatureDescriptionScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.Home.route) {
        composable(Screens.Home.route) {
            HomeScreen(navController)
        }
        composable(
            route = Screens.KanjiWordSet.route,
            arguments = listOf(
                navArgument(Screens.KanjiWordSet.ARG_KANJI) { type = NavType.StringType }
            )
        ) {
            KanjiWordSetScreen(navController)
        }
        composable(
            route = Screens.AiExplanation.route,
            arguments = listOf(
                navArgument(Screens.AiExplanation.ARG_WORD) { type = NavType.StringType },
                navArgument(Screens.AiExplanation.ARG_MEANING) { type = NavType.StringType }
            )
        ) {
            AiExplanationScreen(navController)
        }
        composable(
            route = Screens.TemperatureDescription.route,
            arguments = listOf(
                navArgument(Screens.TemperatureDescription.ARG_KANJI) { type = NavType.StringType },
                navArgument(Screens.TemperatureDescription.ARG_FURIGANA) { type = NavType.StringType },
                navArgument(Screens.TemperatureDescription.ARG_MEANING) { type = NavType.StringType }
            )
        ) {
            TemperatureDescriptionScreen(navController)
        }
    }
}
