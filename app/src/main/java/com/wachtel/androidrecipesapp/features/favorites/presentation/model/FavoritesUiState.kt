package com.wachtel.androidrecipesapp.features.favorites.presentation.model

import androidx.compose.runtime.Immutable
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipeUiModel

@Immutable
data class FavoritesUiState(
    val recipes: List<RecipeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && recipes.isEmpty()
}