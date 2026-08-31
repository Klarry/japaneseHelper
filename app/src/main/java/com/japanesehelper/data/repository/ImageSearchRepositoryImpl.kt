package com.japanesehelper.data.repository

import com.japanesehelper.data.remote.api.ImageSearchApi
import com.japanesehelper.data.remote.dto.ImageSearchRequestDto
import com.japanesehelper.domain.model.SearchResult
import com.japanesehelper.domain.repository.ImageSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageSearchRepositoryImpl @Inject constructor(
    private val imageSearchApi: ImageSearchApi
) : ImageSearchRepository {

    override suspend fun getSearchResults(query: String): SearchResult? = withContext(Dispatchers.IO) {
        val imageBytes = imageSearchApi
            .searchImage(ImageSearchRequestDto(query))
            .bytes()

        imageBytes.takeIf { it.isNotEmpty() }?.let { SearchResult(it) }
    }
}
