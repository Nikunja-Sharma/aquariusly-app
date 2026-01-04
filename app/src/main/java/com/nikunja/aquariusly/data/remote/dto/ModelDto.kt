package com.nikunja.aquariusly.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ModelPricingDto(
    @SerializedName("prompt") val prompt: String,
    @SerializedName("completion") val completion: String
)

data class ModelDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("provider") val provider: String,
    @SerializedName("contextLength") val contextLength: Int,
    @SerializedName("pricing") val pricing: ModelPricingDto
)

data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("hasPrev") val hasPrev: Boolean
)

data class ModelsResponseDto(
    @SerializedName("data") val data: List<ModelDto>,
    @SerializedName("pagination") val pagination: PaginationDto
)

data class ProvidersResponseDto(
    @SerializedName("providers") val providers: List<String>,
    @SerializedName("count") val count: Int
)

data class ModelsCountResponseDto(
    @SerializedName("count") val count: Int
)
