package com.japanesehelper.presentation.screens.aiExplanationScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.japanesehelper.R
import com.japanesehelper.presentation.screens.homeScreen.components.CenteredLoadingIndicator
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.LabeledBlock
import com.japanesehelper.presentation.screens.homeScreen.components.MarkdownText
import com.japanesehelper.presentation.theme.JapaneseHelperTheme
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionError
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionLoading
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionState
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionSuccess

@Composable
fun DescriptionSection(
    state: DescriptionState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is DescriptionLoading -> CenteredLoadingIndicator(modifier)

        is DescriptionError -> ErrorWithRetry(message = state.message, onRetry = onRetry, modifier = modifier)

        is DescriptionSuccess -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                LabeledBlock(caption = stringResource(R.string.ai_explanation_without_constraints_caption)) {
                    MarkdownText(markdown = state.uncontrolled)
                }

                LabeledBlock(caption = stringResource(R.string.ai_explanation_with_constraints_caption)) {
                    MarkdownText(markdown = state.controlled)
                }
            }
        }
    }
}

private const val SHORT_TEXT = "**Sakura** is the Japanese word for the cherry blossom."

private val LONG_TEXT = """
    ### 1. The Meaning of the Word
    **Sakura** refers to the blossom of several ornamental cherry trees of the
    genus *Prunus*, and in Japan it carries a meaning far beyond the plant itself.

    ---

    ### 2. Why It Matters
    Because the petals fall only days after opening, the blossom became a standing
    image of **impermanence**. That image shows up in three places:

    - poetry, where it stands for a short and bright life;
    - seasonal food and packaging every spring;
    - hanami parties held beneath the trees.

    1. Buds open in late March in the south.
    2. The front moves north over several weeks.
    3. Petals fall within about a week of peak bloom.
    ---
""".trimIndent()

@Preview(name = "AI Explanation - short", showBackground = true)
@Composable
fun DescriptionSectionShortPreview() {
    JapaneseHelperTheme {
        DescriptionSection(
            state = DescriptionSuccess(uncontrolled = SHORT_TEXT, controlled = SHORT_TEXT),
            onRetry = {},
            modifier = Modifier.padding(LocalAppPadding.current.defaultAndHalf)
        )
    }
}

@Preview(name = "AI Explanation - long", showBackground = true)
@Composable
fun DescriptionSectionLongPreview() {
    JapaneseHelperTheme {
        DescriptionSection(
            state = DescriptionSuccess(uncontrolled = LONG_TEXT, controlled = SHORT_TEXT),
            onRetry = {},
            modifier = Modifier.padding(LocalAppPadding.current.defaultAndHalf)
        )
    }
}
