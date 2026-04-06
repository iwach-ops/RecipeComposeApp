package com.wachtel.androidrecipesapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wachtel.androidrecipesapp.core.ui.navigation.BottomNavigation
import com.wachtel.androidrecipesapp.ui.categories.CategoriesScreen
import com.wachtel.androidrecipesapp.ui.favorites.FavoritesScreen
import com.wachtel.androidrecipesapp.ui.recipes.RecipesScreen
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesApp() {
    RecipesAppTheme {
        var currentScreen by remember {
            mutableStateOf(ScreenId.CATEGORIES)
        }

        var selectedCategoryId by remember {
            mutableStateOf(-1)
        }

        var selectedCategoryTitle by remember {
            mutableStateOf("Рецепты")
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                BottomNavigation(
                    onCategoriesClick = {
                        currentScreen = ScreenId.CATEGORIES
                    },
                    onRecipesClick = {
                        currentScreen = ScreenId.RECIPES
                    },
                    onFavoriteClick = {
                        currentScreen = ScreenId.FAVORITES
                    }
                )
            }
        ) { paddingValues ->
            when (currentScreen) {
                ScreenId.CATEGORIES -> {
                    CategoriesScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        onCategoryClick = { categoryId, categoryTitle ->
                            selectedCategoryId = categoryId
                            selectedCategoryTitle = categoryTitle
                            currentScreen = ScreenId.RECIPES
                        }
                    )
                }

                ScreenId.RECIPES -> {
                    RecipesScreen(
                        categoryId = selectedCategoryId,
                        categoryTitle = selectedCategoryTitle,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }

                ScreenId.FAVORITES -> {
                    FavoritesScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipesAppPreview() {
    RecipesApp()
}