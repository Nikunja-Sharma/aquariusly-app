package com.nikunja.aquariusly.domain.repository

import com.nikunja.aquariusly.domain.model.ConfigurableModel
import com.nikunja.aquariusly.domain.model.ModelFilters
import com.nikunja.aquariusly.domain.model.PaginatedModels

interface ModelRepository {
    suspend fun getModels(filters: ModelFilters, page: Int, limit: Int): Result<PaginatedModels>
    suspend fun getProviders(): Result<List<String>>
    suspend fun getModelsCount(): Result<Int>
    suspend fun getModelById(id: String): Result<ConfigurableModel>
    suspend fun savePreferredModels(modelIds: List<String>): Result<Unit>
    suspend fun getPreferredModels(): Result<List<String>>
    suspend fun setDefaultModel(modelId: String?): Result<Unit>
    suspend fun getDefaultModel(): Result<String?>
}
