package com.wachtel.androidrecipesapp.features.details.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wachtel.androidrecipesapp.core.PARAM_RECIPE_ID
import com.wachtel.androidrecipesapp.core.utils.FavoriteDataStoreManager
import com.wachtel.androidrecipesapp.data.repository.RecipesRepository
import com.wachtel.androidrecipesapp.features.details.presentation.model.RecipeDetailsUiState
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipeUiModel
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository
) : AndroidViewModel(application) {

    private val favoriteDataStoreManager = FavoriteDataStoreManager(application)

    private var selectedRecipeId: Int =
        savedStateHandle[PARAM_RECIPE_ID] ?: INVALID_RECIPE_ID

    private val recipeFlow = MutableStateFlow<RecipeUiModel?>(null)
    private val isLoadingFlow = MutableStateFlow(false)
    private val errorMessageFlow = MutableStateFlow<String?>(null)

    private val portionsFlow: StateFlow<Int> =
        savedStateHandle.getStateFlow(
            key = KEY_PORTIONS,
            initialValue = RecipeDetailsUiState.DEFAULT_PORTIONS
        )

    private val favoriteIdsFlow = favoriteDataStoreManager
        .getFavoriteIdsFlow()
        .distinctUntilChanged()

    val uiState: StateFlow<RecipeDetailsUiState> = combine(
        recipeFlow,
        favoriteIdsFlow,
        portionsFlow,
        isLoadingFlow,
        errorMessageFlow
    ) { recipe, favoriteIds, portions, isLoading, errorMessage ->
        val recipeWithFavorite = recipe?.copy(
            isFavorite = favoriteIds.contains(recipe.id.toString())
        )

        RecipeDetailsUiState(
            recipe = recipeWithFavorite,
            portions = portions,
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecipeDetailsUiState(
            isLoading = true
        )
    )

    init {
        loadRecipe()
    }

    fun loadRecipe(recipeId: Int) {
        selectedRecipeId = recipeId
        loadRecipe()
    }

    fun loadRecipe() {
        if (selectedRecipeId == INVALID_RECIPE_ID) {
            recipeFlow.value = null
            isLoadingFlow.value = false
            errorMessageFlow.value = null
            return
        }

        viewModelScope.launch {
            isLoadingFlow.value = true
            errorMessageFlow.value = null

            runCatching {
                repository.getRecipe(selectedRecipeId).toUiModel()
            }.onSuccess { recipe ->
                recipeFlow.value = recipe
                isLoadingFlow.value = false
                errorMessageFlow.value = null
            }.onFailure { throwable ->
                recipeFlow.value = null
                isLoadingFlow.value = false
                errorMessageFlow.value = throwable.message ?: ERROR_MESSAGE
            }
        }
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
    }
}