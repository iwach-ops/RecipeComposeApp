package com.wachtel.androidrecipesapp.features.recipes.presentation.model

import androidx.compose.runtime.Immutable
import com.wachtel.androidrecipesapp.core.DEFAULT_CATEGORY_IMAGE_URL
import com.wachtel.androidrecipesapp.core.DEFAULT_CATEGORY_TITLE

@Immutable
data class RecipesUiState(
    val recipes: List<RecipeUiModel> = emptyList(),
    val categoryTitle: String = DEFAULT_CATEGORY_TITLE,
    val categoryImageUrl: String = DEFAULT_CATEGORY_IMAGE_URL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && recipes.isEmpty()

    val hasRecipes: Boolean
        get() = recipes.isNotEmpty()
}