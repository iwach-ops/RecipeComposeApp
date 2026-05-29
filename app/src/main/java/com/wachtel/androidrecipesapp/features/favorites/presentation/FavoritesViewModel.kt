package com.wachtel.androidrecipesapp.features.favorites.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wachtel.androidrecipesapp.core.utils.FavoriteDataStoreManager
import com.wachtel.androidrecipesapp.data.repository.RecipesRepository
import com.wachtel.androidrecipesapp.features.favorites.presentation.model.FavoritesUiState
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(
    application: Application,
    private val repository: RecipesRepository
) : AndroidViewModel(application) {

    private val favoriteDataStoreManager = FavoriteDataStoreManager(application)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FavoritesUiState> = favoriteDataStoreManager
        .getFavoriteIdsFlow()
        .map { favoriteIds ->
            favoriteIds.mapNotNull { idString ->
                idString.toIntOrNull()
            }
        }
        .flatMapLatest { favoriteIds ->
            if (favoriteIds.isEmpty()) {
                flowOf(
                    FavoritesUiState(
                        recipes = emptyList(),
                        isLoading = false,
                        errorMessage = null
                    )
                )
            } else {
                repository
                    .getRecipesByIds(favoriteIds)
                    .map { recipes ->
                        val orderByFavoriteId = favoriteIds
                            .withIndex()
                            .associate { indexedValue ->
                                indexedValue.value to indexedValue.index
                            }

                        FavoritesUiState(
                            recipes = recipes
                                .sortedBy { recipe ->
                                    orderByFavoriteId[recipe.id] ?: Int.MAX_VALUE
                                }
                                .map { recipeDto ->
                                    recipeDto.toUiModel().copy(isFavorite = true)
                                },
                            isLoading = false,
                            errorMessage = null
                        )
                    }
            }
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