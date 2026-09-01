package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.DescriptionRequestDto
import com.japanesehelper.data.remote.dto.DescriptionResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface DescriptionApi {

    /**
     * Generate an uncontrolled and a constrained AI explanation for the given
     * vocabulary meaning.
     *
     * @param request The request containing the vocabulary meaning.
     * @return Both the unconstrained and the format/length/termination-constrained
     * Gemini responses.
     */
    @POST("description")
    suspend fun getDescription(@Body request: DescriptionRequestDto): DescriptionResponseDto
}
