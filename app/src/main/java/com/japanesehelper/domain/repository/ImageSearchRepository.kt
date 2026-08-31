package com.japanesehelper.domain.repository

import com.japanesehelper.domain.model.SearchResult

interface ImageSearchRepository {
    suspend fun getSearchResults(query: String): SearchResult?
}
