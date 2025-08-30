package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Displays a word with a styled caption below it.
 *
 * @param word The main word to display.
 * @param caption The secondary text or description for the word.
 * @param modifier Optional [Modifier] for styling and layout adjustments.
 */
@Composable
fun StyledWord(
    word: String,
    caption: String,
    modifier: Modifier = Modifier
) {
    if (word.isEmpty()) return

    Text(
        modifier = modifier.padding(
            horizontal = LocalAppPadding.current.defaultAndHalf
        ),
        text = buildAnnotatedString {
            append("$caption:")
            append(" ")

            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = LocalAppFontSize.current.font24
                )
            ) {
                append(word)
            }
        },
        style = TextStyle(
            fontSize = LocalAppFontSize.current.font16,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun StyledWordPreview() {
    StyledWord(
        word = "Sakura",
        caption = "Cherry Blossom",
        modifier = Modifier
            .padding(16.dp)
            .background(Color.LightGray)
    )
}
