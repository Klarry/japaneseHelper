package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.japanesehelper.presentation.theme.LocalAppFontSize

@Composable
fun StyledWord(
    word: String,
    caption: String,
    modifier: Modifier = Modifier
) {
    if (word.isEmpty()) return

    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            append("$caption: ")

            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = LocalAppFontSize.current.font24
                )
            ) {
                append(word)
            }
        },
        style = TextStyle(fontSize = LocalAppFontSize.current.font16)
    )
}

@Preview(showBackground = true)
@Composable
fun StyledWordPreview() {
    StyledWord(word = "Sakura", caption = "Cherry Blossom")
}
