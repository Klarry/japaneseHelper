package com.japanesehelper.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AgentTaskStateDto(
    @SerializedName("task_stage") val taskStage: String,
    @SerializedName("current_step") val currentStep: String,
    @SerializedName("expected_action") val expectedAction: String,
    @SerializedName("allowed_next") val allowedNext: List<String>
)
