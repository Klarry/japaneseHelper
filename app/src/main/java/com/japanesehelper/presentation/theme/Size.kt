package com.japanesehelper.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Size(
    val indicatorSmall: Dp = 16.dp,
    val indicator: Dp = 24.dp,
    val strokeWidth: Dp = 2.dp,
    val cardElevation: Dp = 6.dp,
)

val LocalAppSize = staticCompositionLocalOf { Size() }
