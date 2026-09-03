package com.japanesehelper.data.repository

import com.japanesehelper.data.mapper.toDomain
import com.japanesehelper.data.remote.api.TemperatureDescriptionApi
import com.japanesehelper.data.remote.dto.TemperatureDescriptionRequestDto
import com.japanesehelper.domain.model.TemperatureDescription
import com.japanesehelper.domain.repository.TemperatureDescriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TemperatureDescriptionRepositoryImpl @Inject constructor(
    private val temperatureDescriptionApi: TemperatureDescriptionApi
) : TemperatureDescriptionRepository {

    override suspend fun getTemperatureDescription(
        kanji: String,
        temperature: Double
    ): TemperatureDescription = withContext(Dispatchers.IO) {
        temperatureDescriptionApi.getTemperatureDescription(
            TemperatureDescriptionRequestDto(
                kanji = kanji,
                temperature = temperature
            )
        ).toDomain()
    }
}
