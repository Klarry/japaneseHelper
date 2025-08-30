package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.SearchResult

interface GoogleSearchRepository {
    suspend fun getSearchResults(
        apiKey: String,
        cx: String,
        query: String
    ): SearchResult?
}
