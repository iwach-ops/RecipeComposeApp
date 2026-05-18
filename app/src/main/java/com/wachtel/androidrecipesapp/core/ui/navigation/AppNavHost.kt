package com.wachtel.androidrecipesapp.core.ui.navigation

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import com.wachtel.androidrecipesapp.core.PARAM_RECIPE_ID
import com.wachtel.androidrecipesapp.features.details.presentation.RecipeDetailsViewModel
import androidx.lifecycle.SavedStateHandle
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_ID
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_IMAGE_URL
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_TITLE
import androidx.compose.runtime.remember
import com.wachtel.androidrecipesapp.core.network.NetworkConfig
import com.wachtel.androidrecipesapp.core.network.api.RecipesApiService
import com.wachtel.androidrecipesapp.data.repository.RecipesRepositoryImpl
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
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
import com.wachtel.androidrecipesapp.features.categories.ui.CategoriesScreen
import com.wachtel.androidrecipesapp.features.details.ui.RecipeDetailsScreen
import com.wachtel.androidrecipesapp.features.favorites.ui.FavoritesScreen
import com.wachtel.androidrecipesapp.features.recipes.ui.RecipesScreen
import kotlinx.coroutines.delay
import com.wachtel.androidrecipesapp.features.recipes.presentation.RecipesViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    deepLinkIntent: Intent?,
    modifier: Modifier = Modifier
) {
    val apiService = remember {
        createRecipesApiService()
    }

    val repository = remember(apiService) {
        RecipesRepositoryImpl(apiService)
    }
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
                repository = repository,
                modifier = Modifier.fillMaxSize(),
                onCategoryClick = { categoryId, categoryTitle, categoryImageUrl ->
                        val route = Destination.Recipes.createRoute(
                        categoryId = categoryId,
                        categoryTitle = categoryTitle,
                        categoryImageUrl = categoryImageUrl
                    )
                    navController.navigate(route)
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
                },
                navArgument(Destination.Recipes.categoryImageUrlArg) {
                    type = NavType.StringType
                    defaultValue = Destination.Recipes.defaultCategoryImageUrl
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = Destination.Recipes.deepLinkPattern
                }
            )
        ) { backStackEntry ->
            val savedStateHandle = remember(backStackEntry) {
                SavedStateHandle(
                    mapOf(
                        PARAM_CATEGORY_ID to backStackEntry.arguments
                            ?.getInt(Destination.Recipes.categoryIdArg),

                        PARAM_CATEGORY_TITLE to backStackEntry.arguments
                            ?.getString(Destination.Recipes.categoryTitleArg),

                        PARAM_CATEGORY_IMAGE_URL to backStackEntry.arguments
                            ?.getString(Destination.Recipes.categoryImageUrlArg)
                    )
                )
            }

            val viewModel = remember(backStackEntry, repository) {
                RecipesViewModel(
                    savedStateHandle = savedStateHandle,
                    repository = repository
                )
            }

            RecipesScreen(
                viewModel = viewModel,
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
                modifier = Modifier.fillMaxSize(),
                onRecipeClick = { recipeId ->
                    navController.navigate(
                        Destination.RecipeDetails.createRoute(recipeId)
                    )
                }
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
            val context = LocalContext.current

            val savedStateHandle = remember(backStackEntry) {
                SavedStateHandle(
                    mapOf(
                        PARAM_RECIPE_ID to backStackEntry.arguments
                            ?.getInt(Destination.RecipeDetails.recipeIdArg)
                    )
                )
            }

            val viewModel = remember(backStackEntry, repository, context) {
                RecipeDetailsViewModel(
                    application = context.applicationContext as Application,
                    savedStateHandle = savedStateHandle,
                    repository = repository
                )
            }

            RecipeDetailsScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
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
@OptIn(ExperimentalSerializationApi::class)
private fun createRecipesApiService(): RecipesApiService {
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val retrofit = Retrofit.Builder()
        .baseUrl(NetworkConfig.BASE_URL)
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()

    return retrofit.create(RecipesApiService::class.java)
}