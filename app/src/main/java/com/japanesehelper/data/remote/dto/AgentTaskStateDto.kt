package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AgentTaskStateDto(
    @SerializedName("task_stage") val taskStage: String,
    @SerializedName("current_step") val currentStep: String,
    @SerializedName("expected_action") val expectedAction: String,
    @SerializedName("allowed_next") val allowedNext: List<String>,
    @SerializedName("plan") val plan: String? = null,
    @SerializedName("validation_passed") val validationPassed: Boolean? = null,
    @SerializedName("next_requirement") val nextRequirement: String? = null,
    @SerializedName("blocked") val blocked: AgentTaskRefusalDto? = null
)

/** The body of a refused task request: FastAPI wraps it in ``detail``. */
data class AgentTaskErrorDto(
    @SerializedName("detail") val detail: AgentTaskRefusalDto?
)

data class AgentTaskRefusalDto(
    @SerializedName("message") val message: String? = null,
    @SerializedName("current_stage") val currentStage: String? = null,
    @SerializedName("requested_stage") val requestedStage: String? = null,
    @SerializedName("required_next") val requiredNext: List<String>? = null,
    @SerializedName("unmet_condition") val unmetCondition: String? = null
)

data class AgentTaskTransitionRequestDto(
    @SerializedName("task_stage") val taskStage: String
)

data class AgentTaskPlanRequestDto(
    @SerializedName("plan") val plan: String
)

data class AgentTaskValidationRequestDto(
    @SerializedName("passed") val passed: Boolean,
    @SerializedName("notes") val notes: String = ""
)
