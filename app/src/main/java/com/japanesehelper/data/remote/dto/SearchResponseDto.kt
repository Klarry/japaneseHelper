package com.japanesehelper.data.remote.dto

data class SearchResponseDto(
    val items: List<SearchItemDto>
)

data class SearchItemDto(
    val link: String
)
