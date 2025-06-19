package com.japanesehelper.data.mapper

import com.japanesehelper.data.remote.dto.RandomWordDto
import com.japanesehelper.domain.model.RandomWord

fun RandomWordDto.toDomain(): RandomWord {
    return RandomWord(
        word = word,
        furigana = furigana,
        romaji = romaji,
        meaning = meaning
    )
}