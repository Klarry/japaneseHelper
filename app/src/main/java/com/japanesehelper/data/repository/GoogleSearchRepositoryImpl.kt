package com.japanesehelper.data.repository

import com.japanesehelper.data.remote.api.GoogleSearchApi
import com.japanesehelper.domain.model.SearchResult
import com.japanesehelper.domain.repository.GoogleSearchRepository
import javax.inject.Inject

class GoogleSearchRepositoryImpl @Inject constructor(
    val searchApi: GoogleSearchApi
) : GoogleSearchRepository {

    override suspend fun getSearchResults(
        apiKey: String,
        cx: String,
        query: String
    ): SearchResult? {
        return searchApi.searchImages(
            apiKey = apiKey,
            cx = cx,
            query = query
        )
            .items
            .firstOrNull()
            ?.let { item -> SearchResult(item.link) }
    }
}