package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.LocalAppFontSize
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionError
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionLoading
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionState
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionSuccess

/**
 * Displays the AI-generated explanation of a vocabulary meaning: one response
 * generated without constraints and one generated with explicit format, length,
 * and termination constraints, so the difference is obvious side by side.
 *
 * @param state The current [DescriptionState] (loading, error, or success).
 * @param modifier Optional [Modifier] for styling and layout adjustments.
 */
@Composable
fun DescriptionSection(
    state: DescriptionState,
    modifier: Modifier = Modifier
) {
    when (state) {
        is DescriptionLoading -> {
            CircularProgressIndicator()
        }

        is DescriptionError -> Unit

        is DescriptionSuccess -> {
            Column(modifier = modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.home_ai_explanation_title),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = LocalAppFontSize.current.font16
                    )
                )

                DescriptionLine(
                    caption = stringResource(R.string.home_without_constraints_caption),
                    text = state.uncontrolled
                )

                DescriptionLine(
                    caption = stringResource(R.string.home_with_constraints_caption),
                    text = state.controlled
                )
            }
        }
    }
}

@Composable
private fun DescriptionLine(caption: String, text: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                append("$caption: ")
            }
            append(text)
        },
        style = TextStyle(fontSize = LocalAppFontSize.current.font16)
    )
}
