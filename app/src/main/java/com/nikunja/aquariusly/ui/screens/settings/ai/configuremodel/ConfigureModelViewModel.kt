package com.nikunja.aquariusly.ui.screens.settings.ai.configuremodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikunja.aquariusly.domain.model.ConfigurableModel
import com.nikunja.aquariusly.domain.model.ModelFilters
import com.nikunja.aquariusly.domain.model.ModelSortBy
import com.nikunja.aquariusly.domain.model.SortOrder
import com.nikunja.aquariusly.domain.usecase.GetModelsUseCase
import com.nikunja.aquariusly.domain.usecase.GetProvidersUseCase
import com.nikunja.aquariusly.domain.usecase.GetPreferredModelsUseCase
import com.nikunja.aquariusly.domain.usecase.SavePreferredModelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigureModelViewModel @Inject constructor(
    private val getModelsUseCase: GetModelsUseCase,
    private val getProvidersUseCase: GetProvidersUseCase,
    private val savePreferredModelsUseCase: SavePreferredModelsUseCase,
    private val getPreferredModelsUseCase: GetPreferredModelsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ConfigureModelState())
    val state: StateFlow<ConfigureModelState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var currentPage = 1

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Load providers
            getProvidersUseCase().onSuccess { providers ->
                _state.update { it.copy(providers = providers) }
            }
            
            // Load preferred models from backend
            getPreferredModelsUseCase().onSuccess { preferredIds ->
                _state.update { it.copy(selectedModelIds = preferredIds.toSet()) }
            }
            
            // Load models
            loadModels()
        }
    }

    private suspend fun loadModels(page: Int = 1) {
        val filters = _state.value.filters
        getModelsUseCase(filters, page).fold(
            onSuccess = { result ->
                _state.update { state ->
                    val newModels = if (page == 1) {
                        result.models
                    } else {
                        state.models + result.models
                    }
                    state.copy(
                        isLoading = false,
                        models = newModels,
                        pagination = result.pagination,
                        error = null
                    )
                }
                currentPage = page
            },
            onFailure = { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        )
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            _state.update { 
                it.copy(
                    filters = it.filters.copy(search = query.ifBlank { null }),
                    isLoading = true
                )
            }
            loadModels(page = 1)
        }
    }

    fun onProviderSelected(provider: String?) {
        _state.update { 
            it.copy(
                selectedProvider = provider,
                filters = it.filters.copy(provider = provider),
                isLoading = true
            )
        }
        viewModelScope.launch { loadModels(page = 1) }
    }

    fun onSortByChange(sortBy: ModelSortBy?) {
        _state.update { 
            it.copy(
                filters = it.filters.copy(sortBy = sortBy),
                isLoading = true
            )
        }
        viewModelScope.launch { loadModels(page = 1) }
    }

    fun onSortOrderToggle() {
        val newOrder = if (_state.value.filters.sortOrder == SortOrder.ASC) {
            SortOrder.DESC
        } else {
            SortOrder.ASC
        }
        _state.update { 
            it.copy(
                filters = it.filters.copy(sortOrder = newOrder),
                isLoading = true
            )
        }
        viewModelScope.launch { loadModels(page = 1) }
    }

    fun onModelToggle(model: ConfigurableModel) {
        _state.update { state ->
            val newSelected = state.selectedModelIds.toMutableSet()
            if (newSelected.contains(model.id)) {
                newSelected.remove(model.id)
            } else {
                newSelected.add(model.id)
            }
            state.copy(selectedModelIds = newSelected)
        }
    }

    fun onLoadMore() {
        val pagination = _state.value.pagination ?: return
        if (!pagination.hasNext || _state.value.isLoading) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            loadModels(page = currentPage + 1)
        }
    }

    fun onSaveSelection() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            savePreferredModelsUseCase(_state.value.selectedModelIds.toList()).fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false, saveSuccess = true) }
                    delay(1500)
                    _state.update { it.copy(saveSuccess = false) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isSaving = false, error = error.message) }
                }
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
