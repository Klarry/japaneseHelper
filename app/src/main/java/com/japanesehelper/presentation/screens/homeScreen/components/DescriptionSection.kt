package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.JapaneseHelperTheme
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionError
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionLoading
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionState
import com.japanesehelper.presentation.viewmodel.screendata.DescriptionSuccess

/**
 * Displays the AI-generated explanation of a vocabulary meaning: one response
 * generated without constraints and one generated with explicit format, length,
 * and termination constraints, so the difference is obvious side by side.
 *
 * Each response arrives as lightweight Markdown and is rendered through
 * [MarkdownText], so headings, emphasis and lists appear as styled UI rather
 * than raw syntax. The section grows with its content, so the hosting card
 * adapts its height to the response length instead of clipping it.
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
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = LocalAppPadding.current.default),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is DescriptionError -> Unit

        is DescriptionSuccess -> {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text(
                    text = stringResource(R.string.home_ai_explanation_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                DescriptionBlock(
                    caption = stringResource(R.string.home_without_constraints_caption),
                    text = state.uncontrolled
                )

                DescriptionBlock(
                    caption = stringResource(R.string.home_with_constraints_caption),
                    text = state.controlled
                )
            }
        }
    }
}

/**
 * One labelled response: a subsection title followed by the rendered Markdown
 * body on a tinted surface, so the two answers read as separate, tidy units.
 */
@Composable
private fun DescriptionBlock(caption: String, text: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
    ) {
        Text(
            text = caption.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            MarkdownText(
                markdown = text,
                modifier = Modifier.padding(LocalAppPadding.current.default)
            )
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
            modifier = Modifier.padding(LocalAppPadding.current.defaultAndHalf)
        )
    }
}
