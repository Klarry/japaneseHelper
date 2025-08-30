package com.japanesehelper.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class Radius(val radius3: Float = 3f)

val LocalAppRadius = staticCompositionLocalOf { Radius() }
