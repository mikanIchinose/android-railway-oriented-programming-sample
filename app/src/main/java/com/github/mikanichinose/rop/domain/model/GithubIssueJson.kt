package com.github.mikanichinose.rop.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubIssueJson(
    val id: Int,
    val title: String,
    val state: String,
)
