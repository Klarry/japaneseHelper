package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.japanesehelper.presentation.theme.LocalAppPadding

private const val SUBHEADING_LEVEL = 3

/** Renders the model's Markdown reply as styled content, never as raw syntax. */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val blocks = remember(markdown) { markdown.parseMarkdownBlocks() }
    val padding = LocalAppPadding.current

    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEachIndexed { index, block ->
            val spacing = if (index == 0) {
                Dp.Hairline
            } else {
                blocks[index - 1].spacingBefore(block, padding.quarter, padding.half, padding.default)
            }

            when (block) {
                is MarkdownBlock.Heading -> HeadingBlock(block, Modifier.padding(top = spacing))
                is MarkdownBlock.Paragraph -> ParagraphBlock(block, Modifier.padding(top = spacing))
                is MarkdownBlock.ListItem -> ListItemBlock(block, Modifier.padding(top = spacing))
                is MarkdownBlock.Divider -> HorizontalDivider(
                    modifier = Modifier.padding(vertical = padding.default),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

/** Vertical rhythm between two consecutive blocks. */
private fun MarkdownBlock.spacingBefore(next: MarkdownBlock, tight: Dp, normal: Dp, loose: Dp): Dp =
    when {
        next is MarkdownBlock.Divider || this is MarkdownBlock.Divider -> Dp.Hairline
        next is MarkdownBlock.Heading -> loose
        next is MarkdownBlock.ListItem && this is MarkdownBlock.ListItem -> tight
        this is MarkdownBlock.Heading -> normal
        else -> normal
    }

@Composable
private fun HeadingBlock(block: MarkdownBlock.Heading, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = block.text,
        style = if (block.level < SUBHEADING_LEVEL) {
            MaterialTheme.typography.titleMedium
        } else {
            MaterialTheme.typography.titleSmall
        },
        fontWeight = FontWeight.Bold,
        color = LocalContentColor.current
    )
}

@Composable
private fun ParagraphBlock(block: MarkdownBlock.Paragraph, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = block.text,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun ListItemBlock(block: MarkdownBlock.ListItem, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .widthIn(min = LocalAppPadding.current.default)
                .padding(end = LocalAppPadding.current.half),
            text = block.marker,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = block.text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
