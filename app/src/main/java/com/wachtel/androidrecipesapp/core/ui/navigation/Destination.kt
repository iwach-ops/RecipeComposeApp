package com.wachtel.androidrecipesapp.core.ui.navigation

import android.net.Uri
import com.wachtel.androidrecipesapp.core.PARAM_RECIPE_ID
import com.wachtel.androidrecipesapp.core.RECIPE_CUSTOM_HOST
import com.wachtel.androidrecipesapp.core.RECIPE_CUSTOM_SCHEME
import com.wachtel.androidrecipesapp.core.RECIPE_ROUTE

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
        route = "recipes?categoryId={categoryId}&categoryTitle={categoryTitle}&categoryImageUrl={categoryImageUrl}",
        deepLinkPattern = "androidrecipes://recipes?categoryId={categoryId}&categoryTitle={categoryTitle}&categoryImageUrl={categoryImageUrl}"
    ) {
        const val baseRoute = "recipes"

        const val categoryIdArg = "categoryId"
        const val categoryTitleArg = "categoryTitle"
        const val categoryImageUrlArg = "categoryImageUrl"

        const val defaultCategoryId = -1
        const val defaultCategoryTitle = "Рецепты"
        const val defaultCategoryImageUrl = ""

        fun createRoute(
            categoryId: Int = defaultCategoryId,
            categoryTitle: String = defaultCategoryTitle,
            categoryImageUrl: String = defaultCategoryImageUrl
        ): String {
            return "$baseRoute?" +
                    "$categoryIdArg=$categoryId&" +
                    "$categoryTitleArg=${Uri.encode(categoryTitle)}&" +
                    "$categoryImageUrlArg=${Uri.encode(categoryImageUrl)}"
        }

        fun createDeepLink(
            categoryId: Int = defaultCategoryId,
            categoryTitle: String = defaultCategoryTitle,
            categoryImageUrl: String = defaultCategoryImageUrl
        ): String {
            return "androidrecipes://recipes?" +
                    "$categoryIdArg=$categoryId&" +
                    "$categoryTitleArg=${Uri.encode(categoryTitle)}&" +
                    "$categoryImageUrlArg=${Uri.encode(categoryImageUrl)}"
        }
    }

    data object RecipeDetails : Destination(
        route = RECIPE_ROUTE,
        deepLinkPattern = "$RECIPE_CUSTOM_SCHEME://$RECIPE_CUSTOM_HOST/{$PARAM_RECIPE_ID}"
    ) {
        const val recipeIdArg = PARAM_RECIPE_ID

        fun createRoute(recipeId: Int): String {
            return "recipe/$recipeId"
        }
    }
}