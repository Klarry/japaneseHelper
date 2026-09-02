package com.japanesehelper.data.mapper

import com.japanesehelper.data.remote.dto.KanjiWordSetResponseDto
import com.japanesehelper.domain.model.KanjiWordSet

fun KanjiWordSetResponseDto.toDomain(): KanjiWordSet {
    return KanjiWordSet(
        prompt = prompt,
        words = words,
        cost = cost,
        value = value
    )
}
