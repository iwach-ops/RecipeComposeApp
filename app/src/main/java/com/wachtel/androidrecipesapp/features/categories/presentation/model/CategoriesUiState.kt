package com.wachtel.androidrecipesapp.features.categories.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class CategoriesUiState(
    val title: String = "Категории",
    val categories: List<CategoryUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)