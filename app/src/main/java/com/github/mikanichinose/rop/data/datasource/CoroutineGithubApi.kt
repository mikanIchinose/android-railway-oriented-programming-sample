package com.github.mikanichinose.rop.data.datasource

import com.github.mikanichinose.rop.domain.model.GithubIssueJson
import com.github.mikanichinose.rop.domain.model.GithubRepositoryJson
import com.github.mikanichinose.rop.domain.model.GithubUserJson
import com.github.mikanichinose.rop.domain.model.SearchResultJson
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoroutineGithubApi {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GithubUserJson

    @GET("users/mikanIchinose/repos")
    suspend fun getMyRepositories(): List<GithubRepositoryJson>

    @GET("repos/{username}/{repo}")
    suspend fun getRepository(
        @Path("username") username: String,
        @Path("repo") repo: String
    ): GithubRepositoryJson

    @GET("repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): List<GithubIssueJson>

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
    ): SearchResultJson
}