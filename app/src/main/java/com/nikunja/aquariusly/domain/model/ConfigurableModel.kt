package com.nikunja.aquariusly.domain.model

data class ModelPricing(
    val prompt: Double,
    val completion: Double
)

data class ConfigurableModel(
    val id: String,
    val name: String,
    val provider: String,
    val contextLength: Int,
    val pricing: ModelPricing
)

data class ModelPagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean
)

data class PaginatedModels(
    val models: List<ConfigurableModel>,
    val pagination: ModelPagination
)

enum class ModelSortBy(val value: String) {
    NAME("name"),
    PROVIDER("provider"),
    CONTEXT_LENGTH("contextLength"),
    PROMPT_COST("promptCost"),
    COMPLETION_COST("completionCost")
}

enum class SortOrder(val value: String) {
    ASC("asc"),
    DESC("desc")
}

data class ModelFilters(
    val provider: String? = null,
    val search: String? = null,
    val minContext: Int? = null,
    val maxPromptCost: Double? = null,
    val sortBy: ModelSortBy? = null,
    val sortOrder: SortOrder = SortOrder.ASC
)
