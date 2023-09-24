package com.github.mikanichinose.rop

import retrofit2.http.GET
import retrofit2.http.Path

interface ResultGithubApi {
    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Result<GithubUserJson>

    @GET("users/mikanIchinose/repos")
    fun getMyRepositories(): Result<List<GithubRepositoryJson>>

    @GET("repos/{username}/{repo}")
    fun getRepository(
        @Path("username") username: String,
        @Path("repo") repo: String
    ): Result<GithubRepositoryJson>

    @GET("repos/{owner}/{repo}/issues")
    fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): Result<List<GithubIssueJson>>
}