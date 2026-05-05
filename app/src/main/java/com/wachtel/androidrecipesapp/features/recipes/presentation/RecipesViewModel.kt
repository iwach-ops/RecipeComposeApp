package com.wachtel.androidrecipesapp.features.recipes.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wachtel.androidrecipesapp.core.DEFAULT_CATEGORY_ID
import com.wachtel.androidrecipesapp.core.DEFAULT_CATEGORY_IMAGE_URL
import com.wachtel.androidrecipesapp.core.DEFAULT_CATEGORY_TITLE
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_ID
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_IMAGE_URL
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_TITLE
import com.wachtel.androidrecipesapp.data.repository.RecipesRepositoryStub
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipesUiState
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class RecipesViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Int = savedStateHandle[PARAM_CATEGORY_ID] ?: DEFAULT_CATEGORY_ID

    private val categoryTitle: String = decodeArgument(
        savedStateHandle[PARAM_CATEGORY_TITLE] ?: DEFAULT_CATEGORY_TITLE
    )

    private val categoryImageUrl: String = decodeArgument(
        savedStateHandle[PARAM_CATEGORY_IMAGE_URL] ?: DEFAULT_CATEGORY_IMAGE_URL
    )

    private val _uiState = MutableStateFlow(
        RecipesUiState(
            categoryTitle = categoryTitle,
            categoryImageUrl = categoryImageUrl
        )
    )
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                RecipesRepositoryStub
                    .getRecipesByCategoryId(categoryId)
                    .map { recipeDto -> recipeDto.toUiModel() }
            }.onSuccess { recipes ->
                _uiState.update {
                    it.copy(
                        recipes = recipes,
                        categoryTitle = categoryTitle,
                        categoryImageUrl = categoryImageUrl,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Не удалось загрузить рецепты"
                    )
                }
            }
        }
    }

    private fun decodeArgument(value: String): String {
        return runCatching {
            URLDecoder.decode(value, StandardCharsets.UTF_8.name())
        }.getOrDefault(value)
    }
}