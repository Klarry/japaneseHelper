package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.TemperatureDescription

interface TemperatureDescriptionRepository {
    suspend fun getTemperatureDescription(kanji: String, temperature: Double): TemperatureDescription
}
