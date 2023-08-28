package com.github.mikanichinose.result

import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {
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
}