package com.github.mikanichinose.rop

sealed interface ResultSampleError {
    data object UserNotFoundError : ResultSampleError
    data object RepositoryNotFoundError : ResultSampleError
    data object IssueNotFoundError : ResultSampleError
}