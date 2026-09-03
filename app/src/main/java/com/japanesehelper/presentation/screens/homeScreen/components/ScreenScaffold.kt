package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Every screen behind Home: same top bar, same content margins, same vertical
 * rhythm, so the screens read as one application.
 */
@Composable
fun ScreenScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScreenTopBar(title = title, onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(LocalAppPadding.current.default),
            verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default),
            content = content
        )
    }
}
