package com.japanesehelper.presentation.screens.homeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.japanesehelper.presentation.screens.homeScreen.components.Header
import com.japanesehelper.presentation.screens.homeScreen.components.LevelDropdownMenu
import com.japanesehelper.presentation.screens.homeScreen.components.RandomWordCard
import com.japanesehelper.presentation.theme.JapaneseHelperTheme
import com.japanesehelper.presentation.theme.LocalAppPadding

@Suppress("UnusedPrivateMember")
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = LocalAppPadding.current.default),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val modifier = Modifier.padding(horizontal = LocalAppPadding.current.default)

            Header()
            LevelDropdownMenu(modifier = modifier)
            RandomWordCard(modifier = modifier)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RandomWordCardPreview() {
    JapaneseHelperTheme {
        Header()
        RandomWordCard()
    }
}
