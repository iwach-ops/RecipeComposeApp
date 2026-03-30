package com.wachtel.androidrecipesapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wachtel.androidrecipesapp.core.ui.navigation.BottomNavigation
import com.wachtel.androidrecipesapp.ui.categories.CategoriesScreen
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesApp() {
    RecipesAppTheme {
        var currentScreen by remember {
            mutableStateOf(ScreenId.CATEGORIES)
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                BottomNavigation(
                    onCategoriesClick = {
                        currentScreen = ScreenId.CATEGORIES
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
                            .padding(paddingValues)
                    )
                }

                ScreenId.FAVORITES -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Избранное",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
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