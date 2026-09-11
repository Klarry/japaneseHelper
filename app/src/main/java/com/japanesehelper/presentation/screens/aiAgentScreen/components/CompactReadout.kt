package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

private const val VALUE_SEPARATOR = " · "

/** The caption style the readouts under the chat share with LabeledBlock. */
@Composable
fun ReadoutCaption(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

/**
 * A caption and its values on one wrapped line. The conversation owns the
 * height of the screen, so the readouts under the input state their numbers
 * in two lines instead of one row each.
 */
@Composable
fun CompactReadout(caption: String, values: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        ReadoutCaption(caption)
        Text(
            text = values.joinToString(VALUE_SEPARATOR),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Same, for values that have to stay one per line - a window of messages,
 * or a list of facts. */
@Composable
fun CompactLines(caption: String, lines: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        ReadoutCaption(caption)
        lines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
