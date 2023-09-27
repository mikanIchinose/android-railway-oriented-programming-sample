package com.github.mikanichinose.rop.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.mikanichinose.rop.data.datasource.CoroutineGithubApi
import com.github.mikanichinose.rop.domain.model.GithubRepositoryJson
import com.github.mikanichinose.rop.domain.model.RopError
import com.github.mikanichinose.rop.domain.model.SearchResultItemJson
import com.github.mikanichinose.rop.domain.model.SearchResultJson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RepositoryRepository(
    private val coroutineGithubApi: CoroutineGithubApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun searchRepositoryAndGetRandom(
        query: String
    ): Result<GithubRepositoryJson, RopError> = Ok(Unit)
        .map { searchRepositories(query) }
        .map { it.items }
        .flatMap { getRandomRepository(it) }
        .map { fetchRepository(it.owner.login, it.name) }

    private suspend fun searchRepositories(
        query: String
    ): SearchResultJson {
        return withContext(ioDispatcher) {
            coroutineGithubApi.searchRepositories(query)
        }
    }

    private suspend fun fetchRepository(
        owner: String,
        repo: String,
    ): GithubRepositoryJson {
        return withContext(ioDispatcher) {
            coroutineGithubApi.getRepository(owner, repo)
        }
    }

    private fun getRandomRepository(repos: List<SearchResultItemJson>): Result<SearchResultItemJson, RopError> {
        return if (repos.isEmpty()) {
            Err(RopError.RepositoryNotFoundError)
        } else {
            Ok(repos.random())
        }
    }
}