package com.github.mikanichinose.rop.domain.model

sealed interface RopError {
    data object BlankQueryError : RopError
    data object RepositoryNotFoundError : RopError
    data object IssueNotFoundError : RopError
    data class UnknownError(val cause: Throwable) : RopError
}