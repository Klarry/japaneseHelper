package com.japanesehelper.domain.model

/**
 * The four Kanji Word Set prompting strategies the backend supports.
 *
 * Names must match the backend's `ExperimentType` values exactly - they are
 * sent as-is (via `.name`) as the `experimentType` field of the request.
 */
enum class ExperimentType {
    DIRECT,
    STEP_BY_STEP,
    PROMPT,
    EXPERTS
}
