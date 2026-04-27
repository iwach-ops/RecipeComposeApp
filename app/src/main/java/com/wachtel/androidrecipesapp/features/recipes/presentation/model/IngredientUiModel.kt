package com.wachtel.androidrecipesapp.features.recipes.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.compose.runtime.Immutable
import com.wachtel.androidrecipesapp.data.model.IngredientDto

@Immutable
@Parcelize
data class IngredientUiModel(
    val name: String,
    val quantity: String,
    val unitOfMeasure: String
): Parcelable

fun IngredientDto.toUiModel(): IngredientUiModel {
    return IngredientUiModel(
        name = description,
        quantity = quantity,
        unitOfMeasure = unitOfMeasure
    )
}