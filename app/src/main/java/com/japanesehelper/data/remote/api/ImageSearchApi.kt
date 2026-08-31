package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.ImageSearchRequestDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ImageSearchApi {

    /**
     * Search for an image matching the given query.
     *
     * @param request The search request containing the query text.
     * @return Raw image bytes (Content-Type: image/jpeg).
     */
    @POST("image-search")
    suspend fun searchImage(@Body request: ImageSearchRequestDto): ResponseBody
}
