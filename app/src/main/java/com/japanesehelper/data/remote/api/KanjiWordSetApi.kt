package com.japanesehelper.data.remote.api

import com.japanesehelper.data.remote.dto.KanjiWordSetRequestDto
import com.japanesehelper.data.remote.dto.KanjiWordSetResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface KanjiWordSetApi {

    /**
     * Run one Kanji Word Set prompting experiment for the given kanji.
     *
     * A single endpoint serves all four experiment types - the prompt template
     * for [KanjiWordSetRequestDto.experimentType] lives entirely on the backend.
     *
     * @param request The kanji and the experiment type to run.
     * @return The prompt actually used, the selected words, and their total cost/value.
     */
    @POST("kanji-word-set")
    suspend fun getKanjiWordSet(@Body request: KanjiWordSetRequestDto): KanjiWordSetResponseDto
}
