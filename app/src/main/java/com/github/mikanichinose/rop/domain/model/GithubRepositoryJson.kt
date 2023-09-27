package com.github.mikanichinose.rop.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubRepositoryJson(
    val id: Int,
    val name: String,
    val owner: GithubUserJson,
)

fun GithubRepositoryJson.toDomainModel(): RepositoryModel = RepositoryModel(
    id = id,
    name = name,
)
