package com.github.mikanichinose.rop.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResultJson(
    val items: List<SearchResultItemJson>
)

@JsonClass(generateAdapter = true)
data class SearchResultItemJson(
    val name: String,
    @Json(name = "full_name")
    val fullName: String,
    val owner: GithubUserJson,
)
