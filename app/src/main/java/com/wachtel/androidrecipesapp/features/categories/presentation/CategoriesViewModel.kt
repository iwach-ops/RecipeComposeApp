package com.wachtel.androidrecipesapp.features.categories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wachtel.androidrecipesapp.data.repository.RecipesRepository
import com.wachtel.androidrecipesapp.features.categories.presentation.model.CategoriesUiState
import com.wachtel.androidrecipesapp.features.categories.presentation.model.toUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: RecipesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private var categoriesJob: Job? = null

    init {
        loadCategories()
    }

    fun loadCategories() {
        categoriesJob?.cancel()

        categoriesJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            repository
                .getCategories()
                .map { categories ->
                    categories.map { categoryDto ->
                        categoryDto.toUiModel()
                    }
                }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message
                                ?: "Не удалось загрузить категории"
                        )
                    }
                }
                .collect { categories ->
                    _uiState.update {
                        it.copy(
                            categories = categories,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }
}