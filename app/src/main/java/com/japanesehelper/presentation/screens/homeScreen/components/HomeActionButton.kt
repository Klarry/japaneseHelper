package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * One of Home's entry-point buttons (Kanji Word Set / AI Explanation /
 * Temperature Description). Sharing this is what keeps all three the same
 * size and style, and lets a longer label like "Temperature Description"
 * wrap onto a second line instead of overflowing when three sit in a row.
 */
@Composable
fun HomeActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(
            horizontal = LocalAppPadding.current.half,
            vertical = LocalAppPadding.current.half
        ),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
