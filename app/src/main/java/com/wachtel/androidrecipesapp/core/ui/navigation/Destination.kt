package com.wachtel.androidrecipesapp.core.ui.navigation

import com.wachtel.androidrecipesapp.core.DEFAULT_CATEGORY_ID
import com.wachtel.androidrecipesapp.core.DEFAULT_CATEGORY_IMAGE_URL
import com.wachtel.androidrecipesapp.core.DEFAULT_CATEGORY_TITLE
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_ID
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_IMAGE_URL
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_TITLE
import com.wachtel.androidrecipesapp.core.PARAM_RECIPE_ID
import com.wachtel.androidrecipesapp.core.RECIPES_BASE_ROUTE
import com.wachtel.androidrecipesapp.core.RECIPES_DEEP_LINK_PATTERN
import com.wachtel.androidrecipesapp.core.RECIPES_ROUTE
import com.wachtel.androidrecipesapp.core.RECIPE_CUSTOM_HOST
import com.wachtel.androidrecipesapp.core.RECIPE_CUSTOM_SCHEME
import com.wachtel.androidrecipesapp.core.RECIPE_ROUTE
import com.wachtel.androidrecipesapp.core.createRecipesRoute

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
        route = RECIPES_ROUTE,
        deepLinkPattern = RECIPES_DEEP_LINK_PATTERN
    ) {
        const val baseRoute = RECIPES_BASE_ROUTE

        const val categoryIdArg = PARAM_CATEGORY_ID
        const val categoryTitleArg = PARAM_CATEGORY_TITLE
        const val categoryImageUrlArg = PARAM_CATEGORY_IMAGE_URL

        const val defaultCategoryId = DEFAULT_CATEGORY_ID
        const val defaultCategoryTitle = DEFAULT_CATEGORY_TITLE
        const val defaultCategoryImageUrl = DEFAULT_CATEGORY_IMAGE_URL

        fun createRoute(
            categoryId: Int = defaultCategoryId,
            categoryTitle: String = defaultCategoryTitle,
            categoryImageUrl: String = defaultCategoryImageUrl
        ): String {
            return createRecipesRoute(
                categoryId = categoryId,
                categoryTitle = categoryTitle,
                categoryImageUrl = categoryImageUrl
            )
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