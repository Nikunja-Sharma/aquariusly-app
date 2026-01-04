package com.nikunja.aquariusly.ui.screens.settings.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikunja.aquariusly.domain.model.ConfigurableModel
import com.nikunja.aquariusly.domain.model.ModelFilters
import com.nikunja.aquariusly.domain.usecase.GetDefaultModelUseCase
import com.nikunja.aquariusly.domain.usecase.GetModelsUseCase
import com.nikunja.aquariusly.domain.usecase.GetProvidersUseCase
import com.nikunja.aquariusly.domain.usecase.SetDefaultModelUseCase
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
class ModelSelectionViewModel @Inject constructor(
    private val getModelsUseCase: GetModelsUseCase,
    private val getProvidersUseCase: GetProvidersUseCase,
    private val setDefaultModelUseCase: SetDefaultModelUseCase,
    private val getDefaultModelUseCase: GetDefaultModelUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ModelSelectionState())
    val state: StateFlow<ModelSelectionState> = _state.asStateFlow()

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
            
            // Load default model
            getDefaultModelUseCase().onSuccess { defaultModelId ->
                _state.update { it.copy(selectedModelId = defaultModelId) }
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
            delay(300)
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

    fun onModelSelect(model: ConfigurableModel) {
        _state.update { it.copy(selectedModelId = model.id) }
        viewModelScope.launch {
            setDefaultModelUseCase(model.id)
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
}
