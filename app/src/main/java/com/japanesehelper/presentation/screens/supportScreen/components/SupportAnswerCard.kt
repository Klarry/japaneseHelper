package com.japanesehelper.presentation.screens.supportScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import com.japanesehelper.R
import com.japanesehelper.domain.model.ModelProvider
import com.japanesehelper.domain.model.SupportAnswer
import com.japanesehelper.domain.model.SupportStatus
import com.japanesehelper.domain.model.SupportTicket
import com.japanesehelper.presentation.screens.homeScreen.components.LabeledBlock
import com.japanesehelper.presentation.screens.homeScreen.components.MarkdownText
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * One support answer, with what it stands on kept apart: the ticket as the
 * backend's CRM returned it, and the help-centre sources and quotes the
 * backend validated. Everything here is read from the response.
 */
@Composable
fun SupportAnswerCard(answer: SupportAnswer, modifier: Modifier = Modifier) {
    val padding = LocalAppPadding.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(padding.default)
    ) {
        SupportIndicators(answer)

        LabeledBlock(caption = stringResource(R.string.support_answer_title)) {
            Column(verticalArrangement = Arrangement.spacedBy(padding.half)) {
                MarkdownText(markdown = answer.answer)

                if (answer.status == SupportStatus.INSUFFICIENT_DOCUMENTATION) {
                    Text(
                        text = stringResource(R.string.support_insufficient_docs),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        answer.ticket?.let { ticket ->
            LabeledBlock(caption = stringResource(R.string.support_ticket_title, ticket.ticketId)) {
                TicketDetails(ticket = ticket, factsUsed = answer.ticketFactsUsed)
            }
        }

        LabeledBlock(caption = stringResource(R.string.support_docs_title)) {
            SupportEvidence(answer)
        }
    }
}

/** Which ticket, whether its context was loaded, and who answered. */
@Composable
private fun SupportIndicators(answer: SupportAnswer) {
    val isLocal = ModelProvider.entries.firstOrNull { it.wire == answer.provider }?.isLocal == true

    Column {
        Text(
            text = if (answer.ticket != null) {
                stringResource(R.string.support_context_loaded, answer.ticketId)
            } else {
                stringResource(R.string.support_context_missing, answer.ticketId)
            },
            style = MaterialTheme.typography.labelLarge,
            color = if (answer.ticket != null) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )

        Text(
            text = stringResource(
                if (isLocal) R.string.support_provider_local else R.string.support_provider_cloud,
                answer.model.ifBlank { answer.provider },
                answer.totalMs
            ),
            style = MaterialTheme.typography.bodySmall
        )

        if (answer.toolsUsed.isNotEmpty()) {
            Text(
                text = stringResource(R.string.support_tools_used, answer.toolsUsed.joinToString(", ")),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/** The ticket as the CRM holds it. Lines the record does not have are left
 * out rather than filled in. */
@Composable
private fun TicketDetails(ticket: SupportTicket, factsUsed: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.quarter)) {
        DetailLine(R.string.support_ticket_subject, ticket.subject)
        DetailLine(R.string.support_ticket_category, ticket.category)
        DetailLine(R.string.support_ticket_status, ticket.status)
        DetailLine(
            R.string.support_ticket_platform,
            listOf(ticket.platform, ticket.appVersion).filter(String::isNotBlank).joinToString(" ")
        )
        DetailLine(R.string.support_ticket_attempts, ticket.previousAttempts.joinToString(" · "))
        DetailLine(R.string.support_ticket_facts_used, factsUsed.joinToString(", "))
    }
}

@Composable
private fun DetailLine(captionRes: Int, value: String) {
    if (value.isBlank()) return

    Text(
        text = stringResource(captionRes, value),
        style = MaterialTheme.typography.bodySmall
    )
}

/** Help-centre sources and the exact quotes the backend checked - or the
 * plain statement that there are none. */
@Composable
private fun SupportEvidence(answer: SupportAnswer) {
    Column {
        SupportSources(answer)
        SupportCitations(answer)
    }
}

@Composable
private fun SupportSources(answer: SupportAnswer) {
    Text(
        text = stringResource(R.string.ai_agent_rag_sources_title),
        style = MaterialTheme.typography.labelMedium
    )

    if (answer.sources.isEmpty()) {
        Text(text = stringResource(R.string.ai_agent_rag_no_sources), style = MaterialTheme.typography.bodySmall)
    }

    answer.sources.forEach { source ->
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
    }
}

@Composable
private fun SupportCitations(answer: SupportAnswer) {
    Text(
        text = stringResource(R.string.ai_agent_rag_citations_title),
        modifier = Modifier.padding(top = LocalAppPadding.current.quarter),
        style = MaterialTheme.typography.labelMedium
    )

    if (answer.citations.isEmpty()) {
        Text(text = stringResource(R.string.ai_agent_rag_no_citations), style = MaterialTheme.typography.bodySmall)
    }

    answer.citations.forEach { citation ->
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
