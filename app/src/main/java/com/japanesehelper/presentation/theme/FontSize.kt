package com.japanesehelper.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Immutable
data class FontSize(
    val font16: TextUnit = 16.sp,
    val font24: TextUnit = 24.sp,
    val font40: TextUnit = 40.sp,
    val font70: TextUnit = 70.sp
)

val LocalAppFontSize = staticCompositionLocalOf { FontSize() }
