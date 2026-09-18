package com.japanesehelper.domain.model

/**
 * Where the task in progress has got to.
 *
 * The stages and the moves between them live on the backend; the device
 * shows what it is told. ``allowedNext`` comes from the backend too - the
 * state machine reported rather than re-implemented here, so the screen
 * never has to guess which move is legal - and so do ``nextRequirement``
 * (what the stage still owes before the next one can be entered) and
 * ``blocked`` (the last move the backend refused).
 */
data class AgentTaskState(
    val stage: String = IDLE_STAGE,
    val currentStep: String = "",
    val expectedAction: String = "",
    val allowedNext: List<String> = emptyList(),
    val plan: String = "",
    val validationPassed: Boolean = false,
    val nextRequirement: String = "",
    val blocked: AgentTaskRefusal? = null
) {
    val isActive: Boolean get() = stage.isNotBlank() && stage != IDLE_STAGE

    companion object {
        const val IDLE_STAGE = "idle"
    }
}

/**
 * The four stages, by name only.
 *
 * Names, so there is something to put on a button and something to send.
 * Not rules: which of them may follow which, and when, is the backend's
 * answer to give - the screen offers all four and lets it decide.
 */
enum class AgentTaskStage(val wireName: String) {
    PLANNING("planning"),
    EXECUTION("execution"),
    VALIDATION("validation"),
    DONE("done")
}

/**
 * A move the backend refused, exactly as it described it: where the task is,
 * where it was asked to go, what may come next, and what is missing.
 *
 * Nothing here is worked out on the device. ``requestedStage`` is empty when
 * the refusal was not about a move at all (approving a plan or recording a
 * validation from the wrong stage).
 */
data class AgentTaskRefusal(
    val message: String = "",
    val currentStage: String = "",
    val requestedStage: String = "",
    val requiredNext: List<String> = emptyList(),
    val unmetCondition: String = ""
)

/** Raised when the backend answered a task request with a refusal rather
 * than a new state. Carries the refusal so the screen can show it. */
class AgentTaskTransitionRefused(val refusal: AgentTaskRefusal) : Exception(refusal.message)
