package com.wachtel.androidrecipesapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.wachtel.androidrecipesapp.core.ui.navigation.BottomNavigation
import com.wachtel.androidrecipesapp.core.ui.navigation.Destination
import com.wachtel.androidrecipesapp.ui.categories.CategoriesScreen
import com.wachtel.androidrecipesapp.ui.details.RecipeDetailsScreen
import com.wachtel.androidrecipesapp.ui.favorites.FavoritesScreen
import com.wachtel.androidrecipesapp.ui.recipes.RecipesScreen
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesApp() {
    RecipesAppTheme {
        val navController = rememberNavController()

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                BottomNavigation(
                    onCategoriesClick = {
                        navController.navigateToTopLevel(Destination.Categories.route)
                    },
                    onRecipesClick = {
                        navController.navigateToTopLevel(
                            Destination.Recipes.createRoute()
                        )
                    },
                    onFavoriteClick = {
                        navController.navigateToTopLevel(Destination.Favorites.route)
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Destination.Categories.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                    ),
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = Destination.RecipeDetails.deepLinkPattern
                        }
                    )
                ) { backStackEntry ->
                    val recipeId = backStackEntry.arguments
                        ?.getInt(Destination.RecipeDetails.recipeIdArg)
                        ?: return@composable

                    RecipeDetailsScreen(
                        recipeId = recipeId,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun NavController.navigateToTopLevel(route: String) {
    val returnedToExistingDestination = popBackStack(
        route = route,
        inclusive = false
    )

    if (!returnedToExistingDestination) {
        navigate(route) {
            popUpTo(graph.findStartDestination().id)
            launchSingleTop = true
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipesAppPreview() {
    RecipesApp()
}