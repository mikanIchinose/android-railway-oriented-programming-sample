package com.github.mikanichinose.rop

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubUserJson(
    val id: Int,
    val login: String,
    val name: String?,
)
