package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.japanesehelper.R
import com.japanesehelper.presentation.navigation.Screens

/** Day 33: the support assistant. Like the AI Agent button it is not tied to
 * the current word, so it is always enabled. */
@Composable
fun SupportButton(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    HomeActionButton(
        text = stringResource(R.string.home_support_button),
        enabled = true,
        onClick = { navController.navigate(Screens.Support.route) },
        modifier = modifier
    )
}
