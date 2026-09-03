package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.DescriptionRequestDto
import com.japanesehelper.data.remote.dto.DescriptionResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface DescriptionApi {

    @POST("description")
    suspend fun getDescription(@Body request: DescriptionRequestDto): DescriptionResponseDto
}
