package com.wachtel.androidrecipesapp.features.details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wachtel.androidrecipesapp.core.PARAM_RECIPE_ID
import com.wachtel.androidrecipesapp.core.utils.FavoriteDataStoreManager
import com.wachtel.androidrecipesapp.data.repository.RecipesRepository
import com.wachtel.androidrecipesapp.features.details.presentation.model.RecipeDetailsUiState
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipeUiModel
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.toUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository,
    private val favoriteDataStoreManager: FavoriteDataStoreManager
) : ViewModel() {

    private val selectedRecipeId: Int =
        savedStateHandle[PARAM_RECIPE_ID] ?: INVALID_RECIPE_ID

    private val recipeFlow = MutableStateFlow<RecipeUiModel?>(null)
    private val isLoadingFlow = MutableStateFlow(true)
    private val errorMessageFlow = MutableStateFlow<String?>(null)

    private var recipeJob: Job? = null

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
        observeRecipe()
    }

    fun retryRecipe() {
        observeRecipe()
    }

    private fun observeRecipe() {
        recipeJob?.cancel()

        if (selectedRecipeId == INVALID_RECIPE_ID) {
            recipeFlow.value = null
            isLoadingFlow.value = false
            errorMessageFlow.value = null
            return
        }

        recipeJob = viewModelScope.launch {
            isLoadingFlow.value = true
            errorMessageFlow.value = null

            repository
                .getRecipe(selectedRecipeId)
                .catch { throwable ->
                    recipeFlow.value = null
                    isLoadingFlow.value = false
                    errorMessageFlow.value = throwable.message ?: ERROR_MESSAGE
                }
                .collect { recipeDto ->
                    if (recipeDto == null) {
                        recipeFlow.value = null
                        isLoadingFlow.value = true
                        errorMessageFlow.value = null
                    } else {
                        recipeFlow.value = recipeDto.toUiModel()
                        isLoadingFlow.value = false
                        errorMessageFlow.value = null
                    }
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