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

private const val MAX_LABEL_LINES = 2

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
            maxLines = MAX_LABEL_LINES,
            overflow = TextOverflow.Ellipsis
        )
    }
}
