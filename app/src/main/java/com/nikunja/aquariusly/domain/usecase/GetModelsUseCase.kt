package com.nikunja.aquariusly.domain.usecase

import com.nikunja.aquariusly.domain.model.ModelFilters
import com.nikunja.aquariusly.domain.model.PaginatedModels
import com.nikunja.aquariusly.domain.repository.ModelRepository
import javax.inject.Inject

class GetModelsUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(
        filters: ModelFilters = ModelFilters(),
        page: Int = 1,
        limit: Int = 20
    ): Result<PaginatedModels> {
        return repository.getModels(filters, page, limit)
    }
}

class GetProvidersUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getProviders()
    }
}

class SavePreferredModelsUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(modelIds: List<String>): Result<Unit> {
        return repository.savePreferredModels(modelIds)
    }
}

class GetPreferredModelsUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getPreferredModels()
    }
}

class SetDefaultModelUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(modelId: String?): Result<Unit> {
        return repository.setDefaultModel(modelId)
    }
}

class GetDefaultModelUseCase @Inject constructor(
    private val repository: ModelRepository
) {
    suspend operator fun invoke(): Result<String?> {
        return repository.getDefaultModel()
    }
}
