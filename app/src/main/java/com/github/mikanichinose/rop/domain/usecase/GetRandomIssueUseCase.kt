package com.github.mikanichinose.rop.domain.usecase

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.mikanichinose.rop.data.repository.IssueRepository
import com.github.mikanichinose.rop.data.repository.RepositoryRepository
import com.github.mikanichinose.rop.domain.model.IssueModel
import com.github.mikanichinose.rop.domain.model.RopError

typealias IssueResult = Result<IssueModel, RopError>

class GetRandomIssueUseCase(
    private val validateSearchQueryUseCase: ValidateSearchQueryUseCase,
    private val issueRepository: IssueRepository,
    private val repositoryRepository: RepositoryRepository,
) {
    suspend operator fun invoke(query: String): IssueResult {
        return Ok(Unit)
            .flatMap { validateSearchQueryUseCase(query) }
            .flatMap { q -> repositoryRepository.searchRepositoryAndGetRandom(q) }
            .flatMap { repo -> issueRepository.fetchRandomIssue(repo.owner.login, repo.name) }
    }
}