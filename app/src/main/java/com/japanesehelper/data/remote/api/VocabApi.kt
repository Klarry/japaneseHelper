package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.RandomWordDto
import retrofit2.http.GET

interface VocabApi {
    @GET("api/words/random")
    suspend fun getRandomWord(): RandomWordDto
}
