package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentMiniChatAnswer
import com.japanesehelper.domain.model.AgentRagDebug
import com.japanesehelper.domain.model.AgentRagInfo
import com.japanesehelper.domain.model.RagMode
import com.japanesehelper.domain.model.RagStatus
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * Under an answer that went to the document index: what the backend did with
 * the question, which documents it answered from, and the exact words it
 * took from them.
 *
 * Everything here is reported. The counts are the backend's, the status is
 * the backend's, and every quote was checked there against the chunk it
 * names - a quote that was not a real fragment of its document never left
 * the backend, so there is nothing to verify here and nothing to invent. An
 * answer with no evidence is drawn as having no evidence, which is the one
 * thing this block must never soften.
 */
@Composable
fun RagSources(rag: AgentRagInfo, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(modifier = Modifier.padding(LocalAppPadding.current.half)) {
            ReadoutCaption(
                stringResource(
                    // The mini chat runs the same enhanced retrieval, but it
                    // is a conversation rather than one question, and saying
                    // so is the difference the day is about.
                    if (rag.chat != null) R.string.ai_agent_rag_mode_chat else rag.mode.titleRes()
                )
            )

            Text(
                text = stringResource(R.string.ai_agent_rag_status, stringResource(rag.status.labelRes())),
                style = MaterialTheme.typography.bodySmall,
                color = if (rag.status.refused) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            rag.debug?.let { debug -> RagFunnel(debug) }
            rag.chat?.let { chat -> ChatMemory(chat) }

            RagEvidence(rag)
        }
    }
}

/**
 * Sources and citations, or the plain statement that there are none.
 *
 * "No sources" is a result, not a missing section: a refused answer and an
 * answer whose evidence simply was not drawn have to look different.
 */
@Composable
private fun RagEvidence(rag: AgentRagInfo, modifier: Modifier = Modifier) {
    val padding = LocalAppPadding.current

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.ai_agent_rag_sources_title),
            modifier = Modifier.padding(top = padding.quarter),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (rag.sourcesToShow.isEmpty()) {
            Text(
                text = stringResource(R.string.ai_agent_rag_no_sources),
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            rag.sourcesToShow.forEach { source ->
                Text(
                    text = stringResource(R.string.ai_agent_rag_source_row, source.file),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (source.section.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.ai_agent_rag_source_section, source.section),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (source.chunkId.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.ai_agent_rag_source_chunk, source.chunkId),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.ai_agent_rag_citations_title),
            modifier = Modifier.padding(top = padding.quarter),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (rag.citations.isEmpty()) {
            Text(
                text = stringResource(R.string.ai_agent_rag_no_citations),
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            rag.citations.forEach { citation ->
                Text(
                    text = stringResource(R.string.ai_agent_rag_quote, citation.quote),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic
                )

                Text(
                    text = stringResource(R.string.ai_agent_rag_quote_from, citation.source, citation.chunkId),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (rag.chunkCount > 0) {
            Text(
                text = stringResource(R.string.ai_agent_rag_chunks, rag.chunkCount),
                modifier = Modifier.padding(top = padding.quarter),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * The second stage, in four lines: what was asked, what was searched for,
 * and how ten became three.
 *
 * Shown only when the backend sent a debug block, which it does for the
 * enhanced mode alone - the baseline has no funnel to report, and an empty
 * one would read like a funnel that did nothing.
 */
@Composable
private fun RagFunnel(debug: AgentRagDebug, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        if (debug.originalQuery.isNotBlank()) {
            Text(
                text = stringResource(R.string.ai_agent_rag_original_query, debug.originalQuery),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = if (debug.wasRewritten) {
                stringResource(R.string.ai_agent_rag_rewritten_query, debug.rewrittenQuery)
            } else {
                stringResource(R.string.ai_agent_rag_not_rewritten)
            },
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(
                R.string.ai_agent_rag_funnel,
                debug.retrievedCount,
                debug.filteredCount,
                debug.finalCount
            ),
            modifier = Modifier.padding(top = LocalAppPadding.current.quarter),
            style = MaterialTheme.typography.bodySmall
        )

        if (debug.answerThreshold > 0.0) {
            Text(
                text = stringResource(
                    R.string.ai_agent_rag_relevance,
                    debug.bestRelevance,
                    debug.answerThreshold
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * The mini chat's own report (Day 25): what the conversation has settled,
 * and how much was retrieved for this message.
 *
 * This is the part of the day that is worth looking at on a phone. The goal
 * and the constraints are what a long conversation is at risk of losing, so
 * they are shown with every answer rather than hidden behind a switch - and
 * they are the backend's words, read from the response, not anything the
 * screen worked out.
 */
@Composable
private fun ChatMemory(chat: AgentMiniChatAnswer, modifier: Modifier = Modifier) {
    val memory = chat.taskMemory

    Column(modifier = modifier) {
        if (memory.goal.isNotBlank()) {
            Text(
                text = stringResource(R.string.ai_agent_chat_goal, memory.goal),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        ChatMemoryList(R.string.ai_agent_chat_constraints, memory.constraints)
        ChatMemoryList(R.string.ai_agent_chat_decisions, memory.decisions)
        ChatMemoryList(R.string.ai_agent_chat_terms, memory.confirmedTerms)

        Text(
            text = stringResource(
                R.string.ai_agent_chat_counts,
                chat.finalCount,
                chat.sources.size,
                chat.historyLength
            ),
            modifier = Modifier.padding(top = LocalAppPadding.current.quarter),
            style = MaterialTheme.typography.bodySmall
        )

        if (chat.memoryChanges.isNotEmpty()) {
            Text(
                text = stringResource(
                    R.string.ai_agent_chat_changed,
                    chat.memoryChanges.joinToString(", ")
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** One line of the task memory, or nothing when there is nothing to say. */
@Composable
private fun ChatMemoryList(captionRes: Int, entries: List<String>) {
    if (entries.isEmpty()) {
        return
    }

    Text(
        text = stringResource(captionRes, entries.joinToString(" · ")),
        style = MaterialTheme.typography.bodySmall,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

/** What the block above an answer calls itself. */
private fun RagMode.titleRes(): Int = when (this) {
    RagMode.OFF -> R.string.ai_agent_rag_mode_off
    RagMode.BASELINE -> R.string.ai_agent_rag_mode_baseline
    RagMode.ENHANCED -> R.string.ai_agent_rag_mode_enhanced
}

/** What the backend did, in the words the assignment asks for. */
private fun RagStatus.labelRes(): Int = when (this) {
    RagStatus.ANSWERED -> R.string.ai_agent_rag_status_answered
    RagStatus.INSUFFICIENT_CONTEXT -> R.string.ai_agent_rag_status_insufficient
    RagStatus.DISABLED -> R.string.ai_agent_rag_status_disabled
}
