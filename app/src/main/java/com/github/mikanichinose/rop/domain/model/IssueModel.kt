package com.github.mikanichinose.rop.domain.model

data class IssueModel(
    val title: String,
    val state: String,
)

fun GithubIssueJson.toDomainModel(): IssueModel = IssueModel(
    title = title,
    state = state,
)