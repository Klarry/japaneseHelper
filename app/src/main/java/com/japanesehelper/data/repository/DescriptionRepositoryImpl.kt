package com.japanesehelper.data.repository

import com.japanesehelper.data.remote.api.DescriptionApi
import com.japanesehelper.data.remote.dto.DescriptionRequestDto
import com.japanesehelper.domain.model.Description
import com.japanesehelper.domain.repository.DescriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DescriptionRepositoryImpl @Inject constructor(
    private val descriptionApi: DescriptionApi
) : DescriptionRepository {

    override suspend fun getDescription(meaning: String): Description = withContext(Dispatchers.IO) {
        val response = descriptionApi.getDescription(DescriptionRequestDto(meaning))

        Description(
            uncontrolled = response.uncontrolled,
            controlled = response.controlled
        )
    }
}
