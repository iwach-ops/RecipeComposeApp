package com.wachtel.androidrecipesapp

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.wachtel.androidrecipesapp.core.ui.navigation.AppNavHost
import com.wachtel.androidrecipesapp.core.ui.navigation.BottomNavigation
import com.wachtel.androidrecipesapp.core.ui.navigation.Destination
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesApp(
    deepLinkIntent: Intent? = null
) {
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
            AppNavHost(
                navController = navController,
                deepLinkIntent = deepLinkIntent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
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