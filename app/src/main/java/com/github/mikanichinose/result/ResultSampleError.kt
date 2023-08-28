package com.github.mikanichinose.result

sealed interface ResultSampleError {
    data object UserNotFoundError : ResultSampleError
    data object RepositoryNotFoundError : ResultSampleError
    data object IssueNotFoundError : ResultSampleError
}