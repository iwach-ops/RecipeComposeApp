package com.wachtel.androidrecipesapp.features.recipes.presentation.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.wachtel.androidrecipesapp.core.ASSETS_URI_PREFIX
import com.wachtel.androidrecipesapp.data.model.RecipeDto
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class RecipeUiModel(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val ingredients: List<IngredientUiModel>,
    val method: List<String>,
    val isFavorite: Boolean
) : Parcelable

fun RecipeDto.toUiModel(): RecipeUiModel {
    return RecipeUiModel(
        id = id,
        title = title,
        imageUrl = if (imageUrl.startsWith("http", ignoreCase = true)) {
            imageUrl
        } else {
            ASSETS_URI_PREFIX + imageUrl
        },
        ingredients = ingredients.map { it.toUiModel() },
        method = method,
        isFavorite = false
    )
}