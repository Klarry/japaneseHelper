package com.japanesehelper.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.japanesehelper.presentation.screens.homeScreen.HomeScreen
import com.japanesehelper.presentation.screens.kanjiWordSetScreen.KanjiWordSetScreen

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
    }
}
