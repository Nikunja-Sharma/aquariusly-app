package com.nikunja.aquariusly.data.repository

import com.nikunja.aquariusly.data.remote.api.ModelApiService
import com.nikunja.aquariusly.data.remote.api.UserApiService
import com.nikunja.aquariusly.data.remote.dto.ModelDto
import com.nikunja.aquariusly.data.remote.dto.UpdatePreferencesRequest
import com.nikunja.aquariusly.domain.model.ConfigurableModel
import com.nikunja.aquariusly.domain.model.ModelFilters
import com.nikunja.aquariusly.domain.model.ModelPagination
import com.nikunja.aquariusly.domain.model.ModelPricing
import com.nikunja.aquariusly.domain.model.PaginatedModels
import com.nikunja.aquariusly.domain.repository.ModelRepository
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val modelApiService: ModelApiService,
    private val userApiService: UserApiService
) : ModelRepository {

    override suspend fun getModels(
        filters: ModelFilters,
        page: Int,
        limit: Int
    ): Result<PaginatedModels> {
        return try {
            val response = modelApiService.getModels(
                provider = filters.provider,
                search = filters.search,
                minContext = filters.minContext,
                maxPromptCost = filters.maxPromptCost,
                sortBy = filters.sortBy?.value,
                sortOrder = filters.sortOrder.value,
                page = page,
                limit = limit
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(
                    PaginatedModels(
                        models = body.data.map { it.toDomain() },
                        pagination = ModelPagination(
                            page = body.pagination.page,
                            limit = body.pagination.limit,
                            total = body.pagination.total,
                            totalPages = body.pagination.totalPages,
                            hasNext = body.pagination.hasNext,
                            hasPrev = body.pagination.hasPrev
                        )
                    )
                )
            } else {
                Result.failure(Exception("Failed to fetch models"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProviders(): Result<List<String>> {
        return try {
            val response = modelApiService.getProviders()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.providers)
            } else {
                Result.failure(Exception("Failed to fetch providers"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getModelsCount(): Result<Int> {
        return try {
            val response = modelApiService.getModelsCount()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.count)
            } else {
                Result.failure(Exception("Failed to fetch models count"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getModelById(id: String): Result<ConfigurableModel> {
        return try {
            val response = modelApiService.getModelById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Model not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun savePreferredModels(modelIds: List<String>): Result<Unit> {
        return try {
            val response = userApiService.updatePreferences(
                UpdatePreferencesRequest(preferredModels = modelIds)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to save preferred models"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPreferredModels(): Result<List<String>> {
        return try {
            val response = userApiService.getPreferences()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.preferences.preferredModels)
            } else {
                Result.failure(Exception("Failed to fetch preferred models"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setDefaultModel(modelId: String?): Result<Unit> {
        return try {
            val response = userApiService.updatePreferences(
                UpdatePreferencesRequest(defaultModel = modelId)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to set default model"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDefaultModel(): Result<String?> {
        return try {
            val response = userApiService.getPreferences()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.preferences.defaultModel)
            } else {
                Result.failure(Exception("Failed to fetch default model"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ModelDto.toDomain(): ConfigurableModel {
        return ConfigurableModel(
            id = id,
            name = name,
            provider = provider,
            contextLength = contextLength,
            pricing = ModelPricing(
                prompt = pricing.prompt.toDoubleOrNull() ?: 0.0,
                completion = pricing.completion.toDoubleOrNull() ?: 0.0
            )
        )
    }
}
