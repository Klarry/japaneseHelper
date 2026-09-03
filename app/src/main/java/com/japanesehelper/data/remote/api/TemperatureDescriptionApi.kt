package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.TemperatureDescriptionRequestDto
import com.japanesehelper.data.remote.dto.TemperatureDescriptionResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface TemperatureDescriptionApi {

    /**
     * Runs the Temperature Description prompt for the given kanji at one
     * sampling temperature. The prompt template itself lives entirely on the
     * backend - only [TemperatureDescriptionRequestDto.temperature] varies
     * between the three calls this experiment compares.
     */
    @POST("temperature-description")
    suspend fun getTemperatureDescription(
        @Body request: TemperatureDescriptionRequestDto
    ): TemperatureDescriptionResponseDto
}
