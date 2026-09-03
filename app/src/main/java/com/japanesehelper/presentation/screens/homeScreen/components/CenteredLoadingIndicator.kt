package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.japanesehelper.presentation.theme.LocalAppPadding

/** A centered loading spinner for a whole section - the shared loading look. */
@Composable
fun CenteredLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = LocalAppPadding.current.default),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
