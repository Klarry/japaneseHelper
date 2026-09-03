package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.KanjiWordSetRequestDto
import com.japanesehelper.data.remote.dto.KanjiWordSetResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface KanjiWordSetApi {

    @POST("kanji-word-set")
    suspend fun getKanjiWordSet(@Body request: KanjiWordSetRequestDto): KanjiWordSetResponseDto
}
