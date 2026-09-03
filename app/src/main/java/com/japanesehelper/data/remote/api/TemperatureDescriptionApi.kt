package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.TemperatureDescriptionRequestDto
import com.japanesehelper.data.remote.dto.TemperatureDescriptionResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TemperatureDescriptionApi {

    @POST("temperature-description")
    suspend fun getTemperatureDescription(
        @Body request: TemperatureDescriptionRequestDto
    ): TemperatureDescriptionResponseDto
}
