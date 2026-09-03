package com.japanesehelper.domain.model

/** Sent as-is via `.name`, so these must match the backend's `ExperimentType` values. */
enum class ExperimentType {
    DIRECT,
    STEP_BY_STEP,
    PROMPT,
    EXPERTS
}
