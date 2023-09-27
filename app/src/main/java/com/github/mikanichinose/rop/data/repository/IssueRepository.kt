package com.github.mikanichinose.rop.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.mikanichinose.rop.data.datasource.CoroutineGithubApi
import com.github.mikanichinose.rop.domain.model.GithubIssueJson
import com.github.mikanichinose.rop.domain.model.IssueModel
import com.github.mikanichinose.rop.domain.model.RopError
import com.github.mikanichinose.rop.domain.model.toDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class IssueRepository(
    private val coroutineGithubApi: CoroutineGithubApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchRandomIssue(
        owner: String,
        repo: String,
    ): Result<IssueModel, RopError> = Ok(Unit)
        .map { fetchIssues(owner, repo) }
        .flatMap { getRandomIssue(it) }
        .map { it.toDomainModel() }


    private suspend fun fetchIssues(
        owner: String,
        repo: String,
    ): List<GithubIssueJson> {
        return withContext(ioDispatcher) {
            coroutineGithubApi.getIssues(owner, repo)
        }
    }

    private fun getRandomIssue(issues: List<GithubIssueJson>): Result<GithubIssueJson, RopError> {
        return if (issues.isNotEmpty()) {
            Ok(issues.random())
        } else {
            Err(RopError.IssueNotFoundError)
        }
    }
}