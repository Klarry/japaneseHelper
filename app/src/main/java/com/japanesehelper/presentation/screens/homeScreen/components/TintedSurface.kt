package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * A full-width surface on a tinted background, used to set generated/response
 * content apart from the rest of a screen. The one shared shape/color/padding
 * combination behind AI Explanation's, Kanji Word Set's and Temperature
 * Description's response blocks.
 */
@Composable
fun TintedSurface(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(modifier = Modifier.padding(LocalAppPadding.current.default)) {
            content()
        }
    }
}
