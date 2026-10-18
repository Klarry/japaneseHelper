package com.japanesehelper.presentation.screens.supportScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.japanesehelper.R
import com.japanesehelper.presentation.screens.homeScreen.components.CenteredLoadingIndicator
import com.japanesehelper.presentation.screens.homeScreen.components.ErrorWithRetry
import com.japanesehelper.presentation.screens.homeScreen.components.ScreenScaffold
import com.japanesehelper.presentation.screens.homeScreen.components.TintedSurface
import com.japanesehelper.presentation.screens.supportScreen.components.DemoTicketChips
import com.japanesehelper.presentation.screens.supportScreen.components.QuestionField
import com.japanesehelper.presentation.screens.supportScreen.components.SupportAnswerCard
import com.japanesehelper.presentation.screens.supportScreen.components.SupportProviderChips
import com.japanesehelper.presentation.screens.supportScreen.components.TicketIdField
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.viewmodel.SupportViewModel
import com.japanesehelper.presentation.viewmodel.screendata.SupportResult

/**
 * Day 33: ask the support assistant about a ticket.
 *
 * A ticket id, a question and a provider go to POST /support/chat; the
 * backend reads the ticket through its CRM (MCP) and the help centre through
 * RAG, and the screen shows what it sends back.
 */
@Composable
fun SupportScreen(
    navController: NavController,
    viewModel: SupportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ScreenScaffold(
        title = stringResource(R.string.support_title),
        onBack = { navController.popBackStack() }
    ) {
        Text(
            text = stringResource(R.string.support_intro),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        TicketIdField(
            ticketId = state.ticketId,
            showError = state.ticketId.isNotBlank() && !state.isTicketIdValid,
            onChanged = viewModel::onTicketIdChanged
        )

        DemoTicketChips(selected = state.normalizedTicketId, onSelected = viewModel::onTicketIdChanged)

        SupportProviderChips(provider = state.provider, onChanged = viewModel::onProviderChanged)

        QuestionField(question = state.question, onChanged = viewModel::onQuestionChanged)

        Button(
            onClick = viewModel::send,
            enabled = state.canSend,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.support_send_button))
        }

        SupportResultSection(result = state.result, onRetry = viewModel::send)
    }
}

/** Nothing, a spinner, the backend's refusal, or its answer - never an
 * answer made up on the device when the request did not succeed. */
@Composable
private fun SupportResultSection(result: SupportResult, onRetry: () -> Unit) {
    when (result) {
        SupportResult.Idle -> Unit

        SupportResult.Loading -> Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.half)
        ) {
            CenteredLoadingIndicator()
            Text(
                text = stringResource(R.string.support_loading),
                style = MaterialTheme.typography.bodySmall
            )
        }

        is SupportResult.TicketNotFound -> TintedSurface {
            Text(
                text = stringResource(R.string.support_ticket_not_found, result.ticketId),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }

        is SupportResult.Failed -> ErrorWithRetry(message = result.message, onRetry = onRetry)

        is SupportResult.Answered -> SupportAnswerCard(answer = result.answer)
    }
}
