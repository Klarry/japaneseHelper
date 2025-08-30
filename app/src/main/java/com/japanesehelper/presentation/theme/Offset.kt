package com.japanesehelper.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class Offset(
    val spacing5: Float = 5f,
    val spacing10: Float = 10f,
)

val LocalAppOffset = staticCompositionLocalOf { Offset() }
