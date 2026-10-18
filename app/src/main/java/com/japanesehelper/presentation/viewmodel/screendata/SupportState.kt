package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.ModelProvider
import com.japanesehelper.domain.model.SupportAnswer

/** The backend's own format for a ticket id. Checked here only so an obvious
 * typo is caught before a request; the backend checks it again. */
private val TICKET_ID = Regex("^TICKET-\\d{1,8}$", RegexOption.IGNORE_CASE)

/** Ids from the backend's synthetic support data (Day 33), offered as chips
 * so a demo does not depend on typing. They are only ids: nothing about the
 * tickets themselves is known on the device. */
val DEMO_TICKET_IDS = listOf("TICKET-1042", "TICKET-1043", "TICKET-1044", "TICKET-1045")

/** What the last support request came to. */
sealed class SupportResult {
    data object Idle : SupportResult()
    data object Loading : SupportResult()
    data class Answered(val answer: SupportAnswer) : SupportResult()

    /** The backend has no such ticket - and so no answer was written. */
    data class TicketNotFound(val ticketId: String) : SupportResult()

    /** Anything else the backend (or the network) refused with. [message] is
     * shown as it came; nothing is put in its place. */
    data class Failed(val message: String, val error: String = "") : SupportResult()
}

data class SupportScreenState(
    val ticketId: String = "",
    val question: String = "",
    /** Local by default, as on the backend: a support question carries a
     * user's ticket, and the cloud is used only when someone chooses it. */
    val provider: ModelProvider = ModelProvider.LOCAL,
    val result: SupportResult = SupportResult.Idle
) {
    val normalizedTicketId: String get() = ticketId.trim().uppercase()

    val isTicketIdValid: Boolean get() = TICKET_ID.matches(normalizedTicketId)

    val isLoading: Boolean get() = result is SupportResult.Loading

    val canSend: Boolean get() = isTicketIdValid && question.isNotBlank() && !isLoading
}
