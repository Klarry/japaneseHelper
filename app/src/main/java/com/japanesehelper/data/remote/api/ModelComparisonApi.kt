package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.ModelComparisonRequestDto
import com.japanesehelper.data.remote.dto.ModelComparisonResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ModelComparisonApi {

    @POST("model-comparison")
    suspend fun compareModels(
        @Body request: ModelComparisonRequestDto
    ): ModelComparisonResponseDto
}
