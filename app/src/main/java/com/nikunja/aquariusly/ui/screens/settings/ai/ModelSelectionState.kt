package com.nikunja.aquariusly.ui.screens.settings.ai

import com.nikunja.aquariusly.domain.model.ConfigurableModel
import com.nikunja.aquariusly.domain.model.ModelFilters
import com.nikunja.aquariusly.domain.model.ModelPagination

data class ModelSelectionState(
    val isLoading: Boolean = false,
    val models: List<ConfigurableModel> = emptyList(),
    val pagination: ModelPagination? = null,
    val providers: List<String> = emptyList(),
    val selectedModelId: String? = null,
    val filters: ModelFilters = ModelFilters(),
    val searchQuery: String = "",
    val selectedProvider: String? = null,
    val error: String? = null
)
