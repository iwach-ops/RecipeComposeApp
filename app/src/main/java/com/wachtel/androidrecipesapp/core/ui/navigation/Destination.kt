package com.wachtel.androidrecipesapp.core.ui.navigation

import android.net.Uri

sealed class Destination(
    val route: String,
    val deepLinkPattern: String
) {
    object Categories : Destination(
        route = "categories",
        deepLinkPattern = "androidrecipes://categories"
    )

    object Favorites : Destination(
        route = "favorites",
        deepLinkPattern = "androidrecipes://favorites"
    )

    object Recipes : Destination(
        route = "recipes?categoryId={categoryId}&categoryTitle={categoryTitle}",
        deepLinkPattern = "androidrecipes://recipes?categoryId={categoryId}&categoryTitle={categoryTitle}"
    ) {
        const val baseRoute = "recipes"
        const val categoryIdArg = "categoryId"
        const val categoryTitleArg = "categoryTitle"

        const val defaultCategoryId = -1
        const val defaultCategoryTitle = "Рецепты"

        fun createRoute(
            categoryId: Int = defaultCategoryId,
            categoryTitle: String = defaultCategoryTitle
        ): String {
            return "$baseRoute?" +
                    "$categoryIdArg=$categoryId&" +
                    "$categoryTitleArg=${Uri.encode(categoryTitle)}"
        }

        fun createDeepLink(
            categoryId: Int = defaultCategoryId,
            categoryTitle: String = defaultCategoryTitle
        ): String {
            return "androidrecipes://recipes?" +
                    "$categoryIdArg=$categoryId&" +
                    "$categoryTitleArg=${Uri.encode(categoryTitle)}"
        }
    }

    data object RecipeDetails : Destination(
        route = "recipe/{recipeId}",
        deepLinkPattern = "androidrecipes://recipe/{recipeId}"
    ) {
        fun createRoute(recipeId: Int): String = "recipe/$recipeId"
    }
}