package com.wachtel.androidrecipesapp.ui.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.wachtel.androidrecipesapp.ui.recipes.model.RecipeUiModel

@Composable
fun RecipeDetailsScreen(
    recipe: RecipeUiModel
) {
    Text(text = recipe.title)
}