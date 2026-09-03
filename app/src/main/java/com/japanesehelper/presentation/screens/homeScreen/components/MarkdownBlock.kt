package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

sealed interface MarkdownBlock {

    /** [level] is 1 for `#`, 2 for `##` and so on. */
    data class Heading(val level: Int, val text: AnnotatedString) : MarkdownBlock

    data class Paragraph(val text: AnnotatedString) : MarkdownBlock

    /** [marker] is either a bullet glyph or an ordinal such as `2.`. */
    data class ListItem(val marker: String, val text: AnnotatedString) : MarkdownBlock

    data object Divider : MarkdownBlock
}

const val BULLET_GLYPH = "•"

private const val MAX_HEADING_LEVEL = 6

private val HEADING_REGEX = Regex("""^(#{1,6})\s+(.*)$""")
private val THEMATIC_BREAK_REGEX = Regex("""^\s*([-*_])\s*\1\s*\1[\s\-*_]*$""")
private val BULLET_REGEX = Regex("""^\s*[-*+•]\s+(.*)$""")
private val ORDERED_REGEX = Regex("""^\s*(\d{1,3}[.)])\s+(.*)$""")

private val INLINE_REGEX = Regex("""\*\*(.+?)\*\*|__(.+?)__|\*(.+?)\*|`(.+?)`""")

/** Unsupported syntax is kept as plain text, so nothing is ever lost. */
fun String.parseMarkdownBlocks(): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val paragraph = StringBuilder()

    fun flushParagraph() {
        val text = paragraph.toString().trim()
        paragraph.setLength(0)
        if (text.isNotEmpty()) blocks += MarkdownBlock.Paragraph(text.parseInlineMarkdown())
    }

    lines().forEach { rawLine ->
        val line = rawLine.trimEnd()
        val heading = HEADING_REGEX.find(line.trimStart())
        val bullet = BULLET_REGEX.find(line)
        val ordered = ORDERED_REGEX.find(line)

        when {
            line.isBlank() -> flushParagraph()

            THEMATIC_BREAK_REGEX.matches(line) -> {
                flushParagraph()
                blocks += MarkdownBlock.Divider
            }

            heading != null -> {
                flushParagraph()
                val level = heading.groupValues[1].length.coerceAtMost(MAX_HEADING_LEVEL)
                blocks += MarkdownBlock.Heading(level, heading.groupValues[2].parseInlineMarkdown())
            }

            ordered != null -> {
                flushParagraph()
                blocks += MarkdownBlock.ListItem(
                    marker = ordered.groupValues[1],
                    text = ordered.groupValues[2].parseInlineMarkdown()
                )
            }

            bullet != null -> {
                flushParagraph()
                blocks += MarkdownBlock.ListItem(
                    marker = BULLET_GLYPH,
                    text = bullet.groupValues[1].parseInlineMarkdown()
                )
            }

            else -> {
                if (paragraph.isNotEmpty()) paragraph.append(' ')
                paragraph.append(line.trim())
            }
        }
    }

    flushParagraph()
    return blocks.tidyDividers()
}

/** Drops leading, trailing and repeated dividers, which the model emits often. */
private fun List<MarkdownBlock>.tidyDividers(): List<MarkdownBlock> =
    filterIndexed { index, block ->
        if (block !is MarkdownBlock.Divider) return@filterIndexed true
        val previous = getOrNull(index - 1)
        val hasContentBefore = previous != null && previous !is MarkdownBlock.Divider
        val hasContentAfter = subList(index + 1, size).any { it !is MarkdownBlock.Divider }
        hasContentBefore && hasContentAfter
    }

fun String.parseInlineMarkdown(): AnnotatedString = buildAnnotatedString {
    var cursor = 0

    INLINE_REGEX.findAll(this@parseInlineMarkdown).forEach { match ->
        append(this@parseInlineMarkdown.substring(cursor, match.range.first))
        cursor = match.range.last + 1

        val bold = match.groupValues[1].ifEmpty { match.groupValues[2] }
        val italic = match.groupValues[3]
        val code = match.groupValues[4]

        when {
            bold.isNotEmpty() -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(bold) }
            italic.isNotEmpty() -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(italic) }
            code.isNotEmpty() -> withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) { append(code) }
        }
    }

    append(this@parseInlineMarkdown.substring(cursor))
}
