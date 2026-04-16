package com.wachtel.androidrecipesapp.core.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.wachtel.androidrecipesapp.core.RECIPE_CUSTOM_HOST
import com.wachtel.androidrecipesapp.core.RECIPE_CUSTOM_SCHEME
import com.wachtel.androidrecipesapp.core.RECIPE_HOST
import com.wachtel.androidrecipesapp.core.RECIPE_PATH
import com.wachtel.androidrecipesapp.data.repository.RecipesRepositoryStub
import com.wachtel.androidrecipesapp.ui.categories.CategoriesScreen
import com.wachtel.androidrecipesapp.ui.details.RecipeDetailsScreen
import com.wachtel.androidrecipesapp.ui.details.RecipeNotFoundScreen
import com.wachtel.androidrecipesapp.ui.favorites.FavoritesScreen
import com.wachtel.androidrecipesapp.ui.recipes.RecipesScreen
import com.wachtel.androidrecipesapp.ui.recipes.model.toUiModel
import kotlinx.coroutines.delay

@Composable
fun AppNavHost(
    navController: NavHostController,
    deepLinkIntent: Intent?,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(deepLinkIntent) {
        val recipeId = deepLinkIntent?.data?.extractRecipeId() ?: return@LaunchedEffect

        delay(100)

        navController.navigate(Destination.RecipeDetails.createRoute(recipeId)) {
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Destination.Categories.route,
        modifier = modifier
    ) {
        composable(
            route = Destination.Categories.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = Destination.Categories.deepLinkPattern
                }
            )
        ) {
            CategoriesScreen(
                modifier = Modifier.fillMaxSize(),
                onCategoryClick = { categoryId, categoryTitle ->
                    navController.navigate(
                        Destination.Recipes.createRoute(
                            categoryId = categoryId,
                            categoryTitle = categoryTitle
                        )
                    )
                }
            )
        }

        composable(
            route = Destination.Recipes.route,
            arguments = listOf(
                navArgument(Destination.Recipes.categoryIdArg) {
                    type = NavType.IntType
                    defaultValue = Destination.Recipes.defaultCategoryId
                },
                navArgument(Destination.Recipes.categoryTitleArg) {
                    type = NavType.StringType
                    defaultValue = Destination.Recipes.defaultCategoryTitle
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = Destination.Recipes.deepLinkPattern
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments
                ?.getInt(Destination.Recipes.categoryIdArg)
                ?: Destination.Recipes.defaultCategoryId

            val categoryTitle = backStackEntry.arguments
                ?.getString(Destination.Recipes.categoryTitleArg)
                ?: Destination.Recipes.defaultCategoryTitle

            RecipesScreen(
                categoryId = categoryId,
                categoryTitle = categoryTitle,
                modifier = Modifier.fillMaxSize(),
                onRecipeClick = { recipeId ->
                    navController.navigate(
                        Destination.RecipeDetails.createRoute(recipeId)
                    )
                }
            )
        }

        composable(
            route = Destination.Favorites.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = Destination.Favorites.deepLinkPattern
                }
            )
        ) {
            FavoritesScreen(
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = Destination.RecipeDetails.route,
            arguments = listOf(
                navArgument(Destination.RecipeDetails.recipeIdArg) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments
                ?.getInt(Destination.RecipeDetails.recipeIdArg)
                ?: return@composable

            val recipe = RecipesRepositoryStub
                .getRecipeById(recipeId)
                ?.toUiModel()

            if (recipe != null) {
                RecipeDetailsScreen(
                    recipe = recipe,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                RecipeNotFoundScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun Uri.extractRecipeId(): Int? {
    return when {
        scheme.equals(RECIPE_CUSTOM_SCHEME, ignoreCase = true) &&
                host?.equals(RECIPE_CUSTOM_HOST, ignoreCase = true) == true -> {
            pathSegments.firstOrNull()?.toIntOrNull()
        }

        scheme.equals("https", ignoreCase = true) &&
                host?.equals(RECIPE_HOST, ignoreCase = true) == true &&
                pathSegments.firstOrNull() == RECIPE_PATH -> {
            pathSegments.getOrNull(1)?.toIntOrNull()
        }

        else -> null
    }
}