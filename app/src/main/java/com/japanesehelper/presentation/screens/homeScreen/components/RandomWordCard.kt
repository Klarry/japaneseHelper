package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.theme.LocalAppOffset
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.theme.LocalAppRadius
import com.japanesehelper.presentation.theme.LocalAppSize
import com.japanesehelper.presentation.viewmodel.VocabViewModel

@Composable
fun RandomWordCard(
    modifier: Modifier = Modifier,
    viewModel: VocabViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsState()

    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        ElevatedCard(
            onClick = viewModel::getCardData,
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = LocalAppSize.current.cardElevation
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalAppPadding.current.default),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
            ) {
                state.randomWord?.word?.let { word ->
                    Text(
                        text = word,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = LocalAppFontSize.current.font70,
                            shadow = Shadow(
                                color = Color.LightGray,
                                offset = Offset(
                                    LocalAppOffset.current.spacing5,
                                    LocalAppOffset.current.spacing10
                                ),
                                blurRadius = LocalAppRadius.current.radius3
                            )
                        )
                    )
                }

                val picture = state.picture

                if (picture == null) {
                    OutlinedButton(
                        onClick = { viewModel.loadPicture() },
                        enabled = !state.randomWord?.meaning.isNullOrEmpty()
                    ) {
                        Text(stringResource(R.string.home_create_picture_button))
                    }
                } else {
                    ImageWithProgress(data = picture)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = LocalAppPadding.current.default,
                        end = LocalAppPadding.current.default,
                        bottom = LocalAppPadding.current.default
                    ),
                verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.quarter)
            ) {
                StyledWord(
                    word = state.randomWord?.furigana.orEmpty(),
                    caption = stringResource(R.string.home_furigana_caption)
                )
                StyledWord(
                    word = state.randomWord?.romaji.orEmpty(),
                    caption = stringResource(R.string.home_romaji_caption)
                )
                StyledWord(
                    word = state.randomWord?.meaning.orEmpty(),
                    caption = stringResource(R.string.home_meaning_caption)
                )
            }
        }
    }
}
