package com.github.mikanichinose.rop.data.infra

import com.github.mikanichinose.rop.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.GITHUB_API_TOKEN}")
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .build()
        return chain.proceed(request)
    }
}