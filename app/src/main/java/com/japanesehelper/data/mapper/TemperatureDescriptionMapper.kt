package com.japanesehelper.data.mapper

import com.japanesehelper.data.remote.dto.TemperatureDescriptionResponseDto
import com.japanesehelper.domain.model.TemperatureDescription

fun TemperatureDescriptionResponseDto.toDomain(): TemperatureDescription {
    return TemperatureDescription(
        sentence = sentence,
        translation = translation,
        temperature = temperature
    )
}
