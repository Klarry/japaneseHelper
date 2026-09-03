package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * One labelled block: an uppercase caption above a [TintedSurface]. Shared by
 * every screen that shows a captioned response (PROMPT/RESPONSE on Kanji Word
 * Set, the two AI Explanation variants) so they read as one consistent
 * pattern rather than three separately styled ones.
 */
@Composable
fun LabeledBlock(
    caption: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
    ) {
        Text(
            text = caption.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        TintedSurface { content() }
    }
}
