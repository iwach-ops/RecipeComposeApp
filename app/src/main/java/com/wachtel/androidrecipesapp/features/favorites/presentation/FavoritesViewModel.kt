package com.wachtel.androidrecipesapp.features.favorites.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wachtel.androidrecipesapp.core.utils.FavoriteDataStoreManager
import com.wachtel.androidrecipesapp.data.repository.RecipesRepositoryStub
import com.wachtel.androidrecipesapp.features.favorites.presentation.model.FavoritesUiState
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val favoriteDataStoreManager = FavoriteDataStoreManager(application)

    val uiState: StateFlow<FavoritesUiState> = favoriteDataStoreManager
        .getFavoriteIdsFlow()
        .map { favoriteIds ->
            val recipes = favoriteIds
                .mapNotNull { idString ->
                    idString.toIntOrNull()
                }
                .mapNotNull { recipeId ->
                    RecipesRepositoryStub
                        .getRecipeById(recipeId)
                        ?.toUiModel()
                        ?.copy(isFavorite = true)
                }

            FavoritesUiState(
                recipes = recipes,
                isLoading = false,
                errorMessage = null
            )
        }
        .onStart {
            emit(
                FavoritesUiState(
                    isLoading = true
                )
            )
        }
        .catch { throwable ->
            emit(
                FavoritesUiState(
                    isLoading = false,
                    errorMessage = throwable.message ?: ERROR_MESSAGE
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesUiState(
                isLoading = true
            )
        )

    private companion object {
        const val ERROR_MESSAGE = "Не удалось загрузить избранные рецепты"
    }
}