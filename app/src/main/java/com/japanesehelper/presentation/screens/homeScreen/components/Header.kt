package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.JapaneseHelperTheme
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.theme.rainbowColors

@Composable
fun Header(
    modifier: Modifier = Modifier
) {
    val brush = Brush.linearGradient(colors = rainbowColors)

    Text(
        modifier = modifier.padding(vertical = LocalAppPadding.current.half),
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    brush = brush, alpha = .5f, fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(R.string.home_title))
            }
        },
        style = TextStyle(
            fontSize = LocalAppFontSize.current.font40,
        ),
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HeaderPreview() {
    JapaneseHelperTheme {
        Header(modifier = Modifier.fillMaxWidth())
    }
}
