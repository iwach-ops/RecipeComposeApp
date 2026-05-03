package com.wachtel.androidrecipesapp.features.categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wachtel.androidrecipesapp.data.repository.RecipesRepositoryStub
import com.wachtel.androidrecipesapp.features.categories.presentation.model.CategoriesUiState
import com.wachtel.androidrecipesapp.features.categories.presentation.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                RecipesRepositoryStub
                    .getCategories()
                    .map { categoryDto -> categoryDto.toUiModel() }
            }.onSuccess { categories ->
                _uiState.update {
                    it.copy(
                        categories = categories,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Не удалось загрузить категории"
                    )
                }
            }
        }
    }
}