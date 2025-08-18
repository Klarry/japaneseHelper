package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.VocabViewModel

/**
 * A card displaying a random word fetched from [VocabViewModel].
 *
 * @param modifier Optional [Modifier] for styling and layout adjustments.
 * @param viewModel The [VocabViewModel] instance providing the random word data.
 */
@Composable
fun RandomWordCard(
    modifier: Modifier = Modifier,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val randomWord by viewModel.randomWord.collectAsState()
    val pictureData by viewModel.pictureData.collectAsState()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        ElevatedCard(
            onClick = {
                viewModel.getRandomWord()
            },
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

            pictureData?.let { data ->
                ImageWithProgress(
                    modifier = Modifier
                        .padding(bottom = LocalAppPadding.current.default),
                    data = data
                )
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