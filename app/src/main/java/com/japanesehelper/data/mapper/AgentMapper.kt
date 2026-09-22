package com.japanesehelper.data.mapper

import com.google.gson.Gson
import com.japanesehelper.data.remote.dto.AgentChatResponseDto
import com.japanesehelper.data.remote.dto.AgentToolCallDto
import com.japanesehelper.data.remote.dto.AgentContextResponseDto
import com.japanesehelper.data.remote.dto.AgentHistoryMessageDto
import com.japanesehelper.data.remote.dto.AgentHistoryResponseDto
import com.japanesehelper.data.remote.dto.AgentInvariantDto
import com.japanesehelper.data.remote.dto.AgentInvariantsResponseDto
import com.japanesehelper.data.remote.dto.AgentLongTermMemoryDto
import com.japanesehelper.data.remote.dto.AgentMemoryResponseDto
import com.japanesehelper.data.remote.dto.AgentShortTermMemoryDto
import com.japanesehelper.data.remote.dto.AgentTaskErrorDto
import com.japanesehelper.data.remote.dto.AgentTaskRefusalDto
import com.japanesehelper.data.remote.dto.AgentTaskStateDto
import com.japanesehelper.data.remote.dto.AgentUserProfileDto
import com.japanesehelper.data.remote.dto.AgentUserProfileRequestDto
import com.japanesehelper.data.remote.dto.AgentWorkingMemoryDto
import com.japanesehelper.data.remote.dto.AgentUsageDto
import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentInvariant
import com.japanesehelper.domain.model.AgentInvariantCategory
import com.japanesehelper.domain.model.AgentLongTermMemory
import com.japanesehelper.domain.model.AgentMemory
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.model.AgentReply
import com.japanesehelper.domain.model.AgentShortTermMemory
import com.japanesehelper.domain.model.AgentTaskRefusal
import com.japanesehelper.domain.model.AgentTaskState
import com.japanesehelper.domain.model.AgentTokenUsage
import com.japanesehelper.domain.model.AgentToolCall
import com.japanesehelper.domain.model.AgentUserProfile
import com.japanesehelper.domain.model.AgentWorkingMemory

fun AgentChatResponseDto.toDomain(): AgentReply {
    return AgentReply(
        text = response,
        usage = usage.toDomain(),
        toolCalls = toolCalls.orEmpty().mapNotNull { it.toDomain() }
    )
}

/** An entry without a tool name says nothing that can be shown, so it is
 * dropped. Arguments are shown as text: a word, a kanji. */
fun AgentToolCallDto.toDomain(): AgentToolCall? {
    val name = tool?.takeIf { it.isNotBlank() } ?: return null

    return AgentToolCall(
        tool = name,
        arguments = arguments.orEmpty().mapValues { (_, value) -> value?.toString().orEmpty() },
        ok = ok ?: true,
        error = error.orEmpty()
    )
}

fun AgentUsageDto.toDomain(): AgentTokenUsage {
    return AgentTokenUsage(
        currentRequestTokens = currentRequestTokens,
        historyTokens = historyTokens,
        responseTokens = responseTokens,
        totalTokens = totalTokens
    )
}

fun AgentHistoryMessageDto.toDomain(): AgentMessage {
    val mappedRole = if (role == "user") AgentMessageRole.USER else AgentMessageRole.ASSISTANT
    return AgentMessage(role = mappedRole, content = content)
}

fun AgentHistoryResponseDto.toDomain(): List<AgentMessage> = messages.map { it.toDomain() }

fun AgentContextResponseDto.toDomain(): AgentContext {
    return AgentContext(
        strategy = strategy,
        branch = branch,
        branches = branches,
        checkpoints = checkpoints,
        facts = facts,
        messages = messages.map { it.toDomain() }
    )
}

fun AgentShortTermMemoryDto.toDomain(): AgentShortTermMemory =
    AgentShortTermMemory(messages = messages.map { it.toDomain() })

fun AgentWorkingMemoryDto.toDomain(): AgentWorkingMemory = AgentWorkingMemory(
    goals = goals,
    requirements = requirements,
    constraints = constraints,
    decisions = decisions
)

fun AgentLongTermMemoryDto.toDomain(): AgentLongTermMemory = AgentLongTermMemory(
    profile = profile,
    preferences = preferences,
    decisions = decisions,
    knowledge = knowledge
)

fun AgentMemoryResponseDto.toDomain(): AgentMemory = AgentMemory(
    shortTerm = shortTerm.toDomain(),
    working = working.toDomain(),
    longTerm = longTerm.toDomain()
)

fun AgentUserProfileDto.toDomain(): AgentUserProfile = AgentUserProfile(
    japaneseLevel = japaneseLevel,
    explanationStyle = explanationStyle,
    answerFormat = answerFormat,
    translationLanguage = translationLanguage
)

fun AgentUserProfile.toRequestDto(): AgentUserProfileRequestDto = AgentUserProfileRequestDto(
    japaneseLevel = japaneseLevel,
    explanationStyle = explanationStyle,
    answerFormat = answerFormat,
    translationLanguage = translationLanguage
)

fun AgentTaskStateDto.toDomain(): AgentTaskState = AgentTaskState(
    stage = taskStage,
    currentStep = currentStep,
    expectedAction = expectedAction,
    allowedNext = allowedNext,
    plan = plan.orEmpty(),
    validationPassed = validationPassed == true,
    nextRequirement = nextRequirement.orEmpty(),
    blocked = blocked?.toDomain()
)

fun AgentTaskRefusalDto.toDomain(): AgentTaskRefusal = AgentTaskRefusal(
    message = message.orEmpty(),
    currentStage = currentStage.orEmpty(),
    requestedStage = requestedStage.orEmpty(),
    requiredNext = requiredNext.orEmpty(),
    unmetCondition = unmetCondition.orEmpty()
)

/**
 * The refusal out of an error response body, or null if the body is not one.
 *
 * A refusal is the backend's answer, not an outage, so it is read rather
 * than shown as raw JSON - but only if it really is one: anything else
 * (a gateway error page, an empty body) stays an ordinary failure.
 */
private val refusalGson = Gson()

fun parseTaskRefusal(body: String): AgentTaskRefusal? = runCatching {
    refusalGson.fromJson(body, AgentTaskErrorDto::class.java)?.detail?.toDomain()
}.getOrNull()?.takeIf { it.currentStage.isNotBlank() }

/** A rule whose category this build does not know about is dropped rather
 * than shown under a made-up heading - the backend owns that list. */
fun AgentInvariantDto.toDomain(): AgentInvariant? =
    AgentInvariantCategory.fromWireName(category)?.let {
        AgentInvariant(id = id, category = it, rule = rule)
    }

fun AgentInvariantsResponseDto.toDomain(): List<AgentInvariant> = invariants.mapNotNull { it.toDomain() }
