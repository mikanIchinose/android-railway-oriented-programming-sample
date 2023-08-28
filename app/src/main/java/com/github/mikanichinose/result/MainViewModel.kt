package com.github.mikanichinose.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.call.doOnStart
import io.getstream.result.call.map
import io.getstream.result.call.retry
import io.getstream.result.call.retry.RetryPolicy
import io.getstream.result.flatMap
import io.getstream.result.onSuccessSuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainViewModel(
    private val streamGithubApi: StreamGithubApi,
    private val githubApi: GithubApi,
) : ViewModel() {
    private var _repositories: MutableStateFlow<List<RepositoryModel>> =
        MutableStateFlow(emptyList())
    val repositories: StateFlow<List<RepositoryModel>> = _repositories.asStateFlow()

    private var _issues: MutableStateFlow<List<GithubIssueJson>> =
        MutableStateFlow(emptyList())
    val issues: StateFlow<List<GithubIssueJson>> = _issues.asStateFlow()

    fun getMyRepositories(
        onStart: () -> Unit,
        onSuccess: () -> Unit,
        onError: (Error) -> Unit,
    ) {
        viewModelScope.launch {
            val result = streamGithubApi.getMyRepositories()
                .retry(viewModelScope, retryPolicy)
                .doOnStart(viewModelScope, onStart)
                .map { repositories ->
                    repositories.map { it.toDomainModel() }
                }
                .map { flowOf(it) }
                .await()
            result
                .onSuccessSuspend {
                    it.collect {
                        _repositories.value = it
                        onSuccess()
                    }
                }
                .onError(onError)
        }
    }

    fun clearRepositories() {
        _repositories.value = emptyList()
    }

    fun clearIssues() {
        _issues.value = emptyList()
    }

    fun getIssues(
        onLog1: (apiName: String) -> Unit,
        onLog2: (apiName: String) -> Unit,
        onLog3: (apiName: String) -> Unit,
        onSuccess: () -> Unit,
        onError: (Error) -> Unit,
    ) {
        viewModelScope.launch {
            // 1. getUser
            // 2. getRepository use username from getUser result
            // 3. getIssues use username and repo from getRepository result
            val result = Result.Success(Unit)
                .flatMap {
                    streamGithubApi
                        .getUser("mikanIchinose")
                        .retry(viewModelScope, retryPolicy)
                        .doOnStart(viewModelScope) { onLog1("getUser") }
                        .await()
                }
                .flatMap { user ->
                    streamGithubApi
                        .getRepository(user.login, "ddc-gitmoji")
                        .retry(viewModelScope, retryPolicy)
                        .doOnStart(viewModelScope) { onLog2("getRepository") }
                        .await()
                }
                .flatMap { repository ->
                    streamGithubApi
                        .getIssues(repository.owner.login, repository.name)
                        .retry(viewModelScope, retryPolicy)
                        .doOnStart(viewModelScope) { onLog3("getIssues") }
                        .await()
                }

            result
                .onSuccess {
                    _issues.value = it
                    onSuccess()
                }
                .onError(onError)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getIssueFlow(
        onSuccess: () -> Unit,
        onError: (e: Throwable) -> Unit
    ) {
        viewModelScope.launch {
            val flow = flow { emit(Unit) }
                .flatMapConcat {
                    flow {
                        Log.d("MainViewModel", "getUser")
                        emit(githubApi.getUser("mikanIchinose"))
                    }
                }
                .flatMapConcat { user ->
                    flow {
                        Log.d("MainViewModel", "getRepository")
                        emit(githubApi.getRepository(user.login, "ddc-gitmoji"))
                    }
                }
                .flatMapConcat { repository ->
                    flow {
                        Log.d("MainViewModel", "getIssues")
                        emit(
                            githubApi.getIssues(
                                repository.owner.login,
                                repository.name
                            )
                        )
                    }
                }


            flow.catch { e -> onError(e) }
                .collect {
                    _issues.value = it
                    onSuccess()
                }
        }
    }

    private val retryPolicy = object : RetryPolicy {
        override fun retryTimeout(attempt: Int, error: Error): Int = 3000
        override fun shouldRetry(attempt: Int, error: Error): Boolean = attempt < 3
    }

    fun hoge(): kotlin.Result<String> {
        return kotlin.Result.success("hoge")
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(NetworkModule.streamGithubApi, NetworkModule.githubApi) as T
            }
        }
    }
}