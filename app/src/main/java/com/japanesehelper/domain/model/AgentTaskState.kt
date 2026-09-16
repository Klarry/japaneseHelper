package com.japanesehelper.domain.model

/**
 * Where the task in progress has got to.
 *
 * The stages and the moves between them live on the backend; the device
 * shows what it is told. ``allowedNext`` comes from the backend too - the
 * state machine reported rather than re-implemented here, so the screen
 * never has to guess which move is legal.
 */
data class AgentTaskState(
    val stage: String = IDLE_STAGE,
    val currentStep: String = "",
    val expectedAction: String = "",
    val allowedNext: List<String> = emptyList()
) {
    val isActive: Boolean get() = stage.isNotBlank() && stage != IDLE_STAGE

    companion object {
        const val IDLE_STAGE = "idle"
    }
}
