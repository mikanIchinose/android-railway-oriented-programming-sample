package com.github.mikanichinose.rop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.onFailure
import com.github.mikanichinose.rop.data.infra.NetworkModule
import com.github.mikanichinose.rop.data.repository.IssueRepository
import com.github.mikanichinose.rop.data.repository.RepositoryRepository
import com.github.mikanichinose.rop.domain.model.IssueModel
import com.github.mikanichinose.rop.domain.model.RopError
import com.github.mikanichinose.rop.domain.usecase.GetRandomIssueUseCase
import com.github.mikanichinose.rop.domain.usecase.IssueResult
import com.github.mikanichinose.rop.domain.usecase.ValidateSearchQueryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class MainViewModel(
    private val getRandomIssueUseCase: GetRandomIssueUseCase
) : ViewModel() {
    private var _result: MutableStateFlow<IssueResult> =
        MutableStateFlow(Ok(IssueModel("", "")))
    val result: StateFlow<IssueResult> = _result.asStateFlow()

    private var _loading: MutableStateFlow<LoadingState> =
        MutableStateFlow(LoadingState.LOADED)
    val loading: StateFlow<LoadingState> = _loading.asStateFlow()

    fun getRandomIssue(query: String, onValidationError: () -> Unit) {
        viewModelScope.launch {
            flow { emit(getRandomIssueUseCase(query)) }
                .onStart { _loading.value = LoadingState.LOADING }
                .onCompletion { _loading.value = LoadingState.LOADED }
                .catch { e ->
                    if (e is IOException || e is HttpException) {
                        Err(RopError.UnknownError(e))
                    }
                    throw e
                }
                .collect {
                    _result.value = it
                    it.onFailure { error ->
                        if (error is RopError.BlankQueryError) {
                            onValidationError()
                        }
                    }
                }
        }
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val client = NetworkModule.coroutineGithubApi
                val ioDispatcher = Dispatchers.IO
                return MainViewModel(
                    GetRandomIssueUseCase(
                        validateSearchQueryUseCase = ValidateSearchQueryUseCase(),
                        issueRepository = IssueRepository(client, ioDispatcher),
                        repositoryRepository = RepositoryRepository(client, ioDispatcher),
                    )
                ) as T
            }
        }
    }
}

enum class LoadingState {
    LOADING,
    LOADED,
}