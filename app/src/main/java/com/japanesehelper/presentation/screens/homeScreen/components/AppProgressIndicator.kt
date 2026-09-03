package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.japanesehelper.presentation.theme.LocalAppSize

@Composable
fun AppProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = LocalAppSize.current.indicator
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        strokeWidth = LocalAppSize.current.strokeWidth
    )
}
