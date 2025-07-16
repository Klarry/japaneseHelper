package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleSearchApi {

    companion object {
        private const val V1 = "v1"

        private const val DEFAULT_SEARCH_TYPE = "image"
    }

    /**
     * Search images on Google.
     *
     * @param apiKey Google API key.
     * @param cx Custom search engine ID.
     * @param query Search query.
     * @param searchType Search type.
     * @param num Number of results to return.
     */
    @GET("customsearch/$V1")
    suspend fun searchImages(
        @Query("key") apiKey: String,
        @Query("cx") cx: String,
        @Query("q") query: String,
        @Query("searchType") searchType: String = DEFAULT_SEARCH_TYPE,
        @Query("num") num: Int = 1
    ): SearchResponseDto
}