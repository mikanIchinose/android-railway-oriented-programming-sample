package com.github.mikanichinose.rop.domain.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.mikanichinose.rop.domain.model.RopError

class ValidateSearchQueryUseCase {
    operator fun invoke(query: String): Result<String, RopError> {
        return if (query.isNotBlank()) {
            Ok(query)
        } else {
            Err(RopError.BlankQueryError)
        }
    }
}