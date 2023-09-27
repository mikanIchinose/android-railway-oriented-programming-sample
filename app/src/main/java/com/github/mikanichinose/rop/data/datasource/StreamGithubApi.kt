package com.github.mikanichinose.rop.data.datasource

import com.github.mikanichinose.rop.domain.model.GithubIssueJson
import com.github.mikanichinose.rop.domain.model.GithubRepositoryJson
import com.github.mikanichinose.rop.domain.model.GithubUserJson
import io.getstream.result.call.retrofit.RetrofitCall
import retrofit2.http.GET
import retrofit2.http.Path

interface StreamGithubApi {
    @GET("users/{username}")
    fun getUser(@Path("username") username: String): RetrofitCall<GithubUserJson>

    @GET("users/mikanIchinose/repos")
    fun getMyRepositories(): RetrofitCall<List<GithubRepositoryJson>>

    @GET("repos/{username}/{repo}")
    fun getRepository(
        @Path("username") username: String,
        @Path("repo") repo: String
    ): RetrofitCall<GithubRepositoryJson>

    @GET("repos/{owner}/{repo}/issues")
    fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): RetrofitCall<List<GithubIssueJson>>
}