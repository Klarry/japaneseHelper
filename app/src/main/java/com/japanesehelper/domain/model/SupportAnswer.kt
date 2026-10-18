package com.japanesehelper.domain.model

/**
 * What the backend did with a support question (Day 33).
 *
 * [ANSWERED] means at least one quote from the help centre held up on the
 * backend; [INSUFFICIENT_DOCUMENTATION] means none did, and the answer rests
 * on the ticket alone. Reported, never decided here.
 */
enum class SupportStatus(val wire: String) {
    ANSWERED("answered"),
    INSUFFICIENT_DOCUMENTATION("insufficient_documentation");

    companion object {
        /** Anything unrecognised reads as [ANSWERED]: an answer that arrived
         * is still an answer, and its (possibly empty) evidence says the rest. */
        fun from(wire: String?): SupportStatus =
            entries.firstOrNull { it.wire.equals(wire, ignoreCase = true) } ?: ANSWERED
    }
}

/** The ticket as the backend's CRM holds it - the context the answer was
 * written with. Shown, not edited: the device has no copy of its own. */
data class SupportTicket(
    val ticketId: String,
    val status: String = "",
    val category: String = "",
    val subject: String = "",
    val platform: String = "",
    val appVersion: String = "",
    val previousAttempts: List<String> = emptyList(),
    val userLanguage: String = ""
)

/**
 * One support answer and what it stands on.
 *
 * [sources] and [citations] are documentation, checked on the backend
 * against the help-centre chunks they name; [ticket] is the case record.
 * Kept apart so the screen can show which is which.
 */
data class SupportAnswer(
    val answer: String,
    val ticketId: String,
    val status: SupportStatus = SupportStatus.ANSWERED,
    val sources: List<AgentRagSource> = emptyList(),
    val citations: List<AgentRagCitation> = emptyList(),
    val ticket: SupportTicket? = null,
    val ticketFactsUsed: List<String> = emptyList(),
    val toolsUsed: List<String> = emptyList(),
    val provider: String = "",
    val model: String = "",
    val totalMs: Int = 0
) {
    val hasDocumentation: Boolean get() = sources.isNotEmpty() || citations.isNotEmpty()
}

/** The backend has no such ticket. Its own words are in [message]. */
class SupportTicketNotFound(val ticketId: String, message: String) : RuntimeException(message)

/** The backend refused to answer for another reason it named ([error] is
 * its code, e.g. provider_unavailable or ticket_user_not_found). */
class SupportRequestFailed(val error: String, message: String) : RuntimeException(message)
