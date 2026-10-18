package com.japanesehelper.data.mapper

import com.google.gson.Gson
import com.japanesehelper.data.remote.dto.SupportChatErrorDto
import com.japanesehelper.data.remote.dto.SupportChatResponseDto
import com.japanesehelper.data.remote.dto.SupportTicketDto
import com.japanesehelper.domain.model.SupportAnswer
import com.japanesehelper.domain.model.SupportStatus
import com.japanesehelper.domain.model.SupportTicket

private val supportGson = Gson()

/** POST /support/chat -> what the screen shows. Sources and citations go
 * through the same mappers as every other RAG answer, so a citation without
 * a quote or a chunk is dropped here exactly as it is there. */
fun SupportChatResponseDto.toDomain(): SupportAnswer = SupportAnswer(
    answer = answer.orEmpty(),
    ticketId = ticketId.orEmpty(),
    status = SupportStatus.from(status),
    sources = sources.orEmpty().mapNotNull { it.toDomain() },
    citations = citations.orEmpty().mapNotNull { it.toDomain() },
    ticket = ticket?.toDomain(),
    ticketFactsUsed = ticketFactsUsed.orEmpty(),
    toolsUsed = toolsUsed.orEmpty().mapNotNull { it.name?.takeIf(String::isNotBlank) },
    provider = provider.orEmpty(),
    model = model.orEmpty(),
    totalMs = timing?.totalMs ?: 0
)

/** A ticket without an id is not a ticket the screen can name. */
fun SupportTicketDto.toDomain(): SupportTicket? {
    val id = ticketId?.takeIf { it.isNotBlank() } ?: return null

    return SupportTicket(
        ticketId = id,
        status = status.orEmpty(),
        category = category.orEmpty(),
        subject = subject.orEmpty(),
        platform = platform.orEmpty(),
        appVersion = appVersion.orEmpty(),
        previousAttempts = previousAttempts.orEmpty(),
        userLanguage = userLanguage.orEmpty()
    )
}

/** The backend's error body, when it is one of the support errors. A
 * validation error (422, whose detail is a list) or anything else that does
 * not parse is null, and the caller keeps the original exception. */
fun parseSupportError(body: String): SupportChatErrorDto? = runCatching {
    supportGson.fromJson(body, SupportChatErrorDto::class.java)
}.getOrNull()?.takeIf { !it.error.isNullOrBlank() }
