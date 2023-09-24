package com.github.mikanichinose.rop

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubIssueJson(
    val id: Int,
    val title: String,
    val state: String,
)