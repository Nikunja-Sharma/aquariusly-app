package com.nikunja.aquariusly.data.remote.api

import com.nikunja.aquariusly.data.remote.dto.ModelDto
import com.nikunja.aquariusly.data.remote.dto.ModelsCountResponseDto
import com.nikunja.aquariusly.data.remote.dto.ModelsResponseDto
import com.nikunja.aquariusly.data.remote.dto.ProvidersResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ModelApiService {

    @GET("models")
    suspend fun getModels(
        @Query("provider") provider: String? = null,
        @Query("search") search: String? = null,
        @Query("minContext") minContext: Int? = null,
        @Query("maxPromptCost") maxPromptCost: Double? = null,
        @Query("maxCompletionCost") maxCompletionCost: Double? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ModelsResponseDto>

    @GET("models/providers")
    suspend fun getProviders(): Response<ProvidersResponseDto>

    @GET("models/count")
    suspend fun getModelsCount(): Response<ModelsCountResponseDto>

    @GET("models/{id}")
    suspend fun getModelById(@Path("id") id: String): Response<ModelDto>
}
