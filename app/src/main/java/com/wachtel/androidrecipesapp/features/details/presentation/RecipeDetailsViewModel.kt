package com.wachtel.androidrecipesapp.features.details.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wachtel.androidrecipesapp.core.PARAM_RECIPE_ID
import com.wachtel.androidrecipesapp.core.utils.FavoriteDataStoreManager
import com.wachtel.androidrecipesapp.data.repository.RecipesRepositoryStub
import com.wachtel.androidrecipesapp.features.details.presentation.model.RecipeDetailsUiState
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val recipeId: Int = savedStateHandle[PARAM_RECIPE_ID] ?: INVALID_RECIPE_ID

    private val favoriteDataStoreManager = FavoriteDataStoreManager(application)

    private val reloadTrigger = MutableStateFlow(0)

    private val portionsFlow = MutableStateFlow(
        RecipeDetailsUiState.DEFAULT_PORTIONS
    )

    private val recipeFlow = reloadTrigger.map {
        RecipesRepositoryStub
            .getRecipeById(recipeId)
            ?.toUiModel()
    }

    private val isFavoriteFlow = favoriteDataStoreManager
        .isFavoriteFlow(recipeId)
        .distinctUntilChanged()

    val uiState: StateFlow<RecipeDetailsUiState> = combine(
        recipeFlow,
        isFavoriteFlow,
        portionsFlow
    ) { recipe, isFavorite, portions ->
        RecipeDetailsUiState(
            recipe = recipe?.copy(isFavorite = isFavorite),
            portions = portions,
            isLoading = false,
            errorMessage = null
        )
    }
        .onStart {
            emit(
                RecipeDetailsUiState(
                    isLoading = true
                )
            )
        }
        .catch { throwable ->
            emit(
                RecipeDetailsUiState(
                    isLoading = false,
                    errorMessage = throwable.message ?: ERROR_MESSAGE
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecipeDetailsUiState(
                isLoading = true
            )
        )

    fun loadRecipe() {
        reloadTrigger.update { it + 1 }
    }

    fun updatePortions(portions: Int) {
        portionsFlow.value = portions.coerceIn(
            minimumValue = RecipeDetailsUiState.MIN_PORTIONS,
            maximumValue = RecipeDetailsUiState.MAX_PORTIONS
        )
    }

    fun toggleFavorite() {
        val recipe = uiState.value.recipe ?: return

        viewModelScope.launch {
            if (recipe.isFavorite) {
                favoriteDataStoreManager.removeFavorite(recipe.id)
            } else {
                favoriteDataStoreManager.addFavorite(recipe.id)
            }
        }
    }

    private companion object {
        const val INVALID_RECIPE_ID = -1
        const val ERROR_MESSAGE = "Не удалось загрузить рецепт"
    }
}