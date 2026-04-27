package com.wachtel.androidrecipesapp.core.utils

import android.content.Context
import android.content.Intent
import com.wachtel.androidrecipesapp.core.createRecipeDeepLink

fun shareRecipe(
    context: Context,
    recipeId: Int,
    recipeTitle: String
) {
    val deepLink = createRecipeDeepLink(recipeId)

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, recipeTitle)
        putExtra(
            Intent.EXTRA_TEXT,
            "Посмотри рецепт \"$recipeTitle\": $deepLink"
        )
    }

    context.startActivity(
        Intent.createChooser(shareIntent, "Поделиться рецептом")
    )
}