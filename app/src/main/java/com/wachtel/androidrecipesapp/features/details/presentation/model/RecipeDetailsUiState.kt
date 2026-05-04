package com.wachtel.androidrecipesapp.features.details.presentation.model

import androidx.compose.runtime.Immutable
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.IngredientUiModel
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipeUiModel
import java.util.Locale

@Immutable
data class RecipeDetailsUiState(
    val recipe: RecipeUiModel? = null,
    val portions: Int = DEFAULT_PORTIONS,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isFavorite: Boolean
        get() = recipe?.isFavorite ?: false

    val recalculatedIngredients: List<IngredientUiModel>
        get() = recipe?.ingredients.orEmpty().map { ingredient ->
            ingredient.copy(
                quantity = ingredient.quantity.scaleToPortions(portions)
            )
        }

    companion object {
        const val MIN_PORTIONS = 1
        const val MAX_PORTIONS = 8
        const val DEFAULT_PORTIONS = 1
    }
}

private fun String.scaleToPortions(portions: Int): String {
    val numericValue = replace(',', '.').toDoubleOrNull() ?: return this
    val scaledValue = numericValue * portions

    val formatted = if (scaledValue % 1.0 == 0.0) {
        scaledValue.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", scaledValue)
            .trimEnd('0')
            .trimEnd('.')
    }

    return formatted.replace('.', ',')
}