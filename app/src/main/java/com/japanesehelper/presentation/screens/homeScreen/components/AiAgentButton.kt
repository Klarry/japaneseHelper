package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.japanesehelper.R
import com.japanesehelper.presentation.navigation.Screens

/**
 * Unlike the other Home buttons, the AI Agent screen isn't tied to the
 * current random word - it's a standalone request/response demo, so this
 * navigates with no arguments and is always enabled.
 */
@Composable
fun AiAgentButton(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    HomeActionButton(
        text = stringResource(R.string.home_ai_agent_button),
        enabled = true,
        onClick = { navController.navigate(Screens.AiAgent.route) },
        modifier = modifier
    )
}
