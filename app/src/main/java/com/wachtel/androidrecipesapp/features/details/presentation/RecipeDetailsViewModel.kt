package com.wachtel.androidrecipesapp.features.details.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
    private val savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {

    private val favoriteDataStoreManager = FavoriteDataStoreManager(application)

    private val initialRecipeId: Int =
        savedStateHandle[PARAM_RECIPE_ID] ?: INVALID_RECIPE_ID

    private val selectedRecipeIdFlow = MutableStateFlow(initialRecipeId)

    private val reloadTrigger = MutableStateFlow(0)

    private val portionsFlow: StateFlow<Int> =
        savedStateHandle.getStateFlow(
            key = KEY_PORTIONS,
            initialValue = RecipeDetailsUiState.DEFAULT_PORTIONS
        )

    private val recipeFlow = combine(
        selectedRecipeIdFlow,
        reloadTrigger
    ) { recipeId, _ ->
        recipeId
    }.map { recipeId ->
        if (recipeId == INVALID_RECIPE_ID) {
            null
        } else {
            RecipesRepositoryStub
                .getRecipeById(recipeId)
                ?.toUiModel()
        }
    }

    private val favoriteIdsFlow = favoriteDataStoreManager
        .getFavoriteIdsFlow()
        .distinctUntilChanged()

    val uiState: StateFlow<RecipeDetailsUiState> = combine(
        recipeFlow,
        favoriteIdsFlow,
        portionsFlow
    ) { recipe, favoriteIds, portions ->
        val isFavorite = recipe?.let {
            favoriteIds.contains(it.id.toString())
        } ?: false

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

    fun loadRecipe(recipeId: Int) {
        selectedRecipeIdFlow.value = recipeId
        reloadTrigger.update { it + 1 }
    }

    fun loadRecipe() {
        loadRecipe(selectedRecipeIdFlow.value)
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

    fun updatePortions(count: Int) {
        val correctedCount = count.coerceIn(
            minimumValue = RecipeDetailsUiState.MIN_PORTIONS,
            maximumValue = RecipeDetailsUiState.MAX_PORTIONS
        )

        savedStateHandle[KEY_PORTIONS] = correctedCount
    }
    companion object {
        private const val INVALID_RECIPE_ID = -1
        private const val ERROR_MESSAGE = "Не удалось загрузить рецепт"
        private const val KEY_PORTIONS = "portions"

        val Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()

                val application = checkNotNull(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                )

                RecipeDetailsViewModel(
                    savedStateHandle = savedStateHandle,
                    application = application
                )
            }
        }
    }
}