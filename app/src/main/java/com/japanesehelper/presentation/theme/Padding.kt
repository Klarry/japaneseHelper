package com.japanesehelper.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Padding(
    val quarter: Dp = 4.dp,
    val half: Dp = 8.dp,
    val default: Dp = 16.dp,
    val defaultAndHalf: Dp = 24.dp,
    val double: Dp = 32.dp,
)

val LocalAppPadding = staticCompositionLocalOf { Padding() }