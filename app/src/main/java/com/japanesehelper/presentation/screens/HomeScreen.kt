package com.japanesehelper.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.JapaneseHelperTheme
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.theme.rainbowColors
import com.japanesehelper.presentation.viewmodel.VocabViewModel

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()
            RandomWordCard()
        }
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier
) {
    val brush = Brush.linearGradient(colors = rainbowColors)

    Text(
        modifier = modifier
            .padding(
                horizontal = LocalAppPadding.current.defaultAndHalf,
                vertical = LocalAppPadding.current.default
            ),
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

@Composable
fun RandomWordCard(
    modifier: Modifier = Modifier,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val randomWord by viewModel.randomWord.collectAsState()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        ElevatedCard(
            onClick = { viewModel.getRandomWord() },
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 6.dp
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(LocalAppPadding.current.default)
                .fillMaxWidth()

        ) {

            Column(
                modifier = Modifier
                    .padding(LocalAppPadding.current.default)
                    .fillMaxWidth()
            ) {
                randomWord?.word?.let {
                    Text(
                        text = it,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = LocalAppFontSize.current.font70,
                            shadow = Shadow(
                                color = Color.LightGray,
                                offset = Offset(5.0f, 10.0f),
                                blurRadius = 3f
                            )
                        )
                    )
                }
            }

            StyledWord(
                word = randomWord?.furigana.orEmpty(),
                caption = stringResource(R.string.home_furigana_caption)
            )

            StyledWord(
                word = randomWord?.romaji.orEmpty(),
                caption = stringResource(R.string.home_romaji_caption)
            )

            StyledWord(
                word = randomWord?.meaning.orEmpty(),
                caption = stringResource(R.string.home_meaning_caption),
                modifier = Modifier.padding(bottom = LocalAppPadding.current.defaultAndHalf),
            )
        }
    }
}

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
fun RandomWordCardPreview() {
    JapaneseHelperTheme {
        RandomWordCard()
    }
}