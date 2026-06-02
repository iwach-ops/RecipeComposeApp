package com.wachtel.androidrecipesapp.features.recipes.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.IngredientUiModel
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipeUiModel
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipesUiState
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme
import org.junit.Rule
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipesContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsLoadingState() {
        composeTestRule.setContent {
            RecipesAppTheme {
                RecipesContent(
                    uiState = RecipesUiState(
                        isLoading = true
                    ),
                    onRecipeClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }

    @Test
    fun showsErrorState() {
        composeTestRule.setContent {
            RecipesAppTheme {
                RecipesContent(
                    uiState = RecipesUiState(
                        errorMessage = "Network error"
                    ),
                    onRecipeClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag("error_message")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Network error")
            .assertIsDisplayed()
    }

    @Test
    fun showsEmptyState() {
        composeTestRule.setContent {
            RecipesAppTheme {
                RecipesContent(
                    uiState = RecipesUiState(),
                    onRecipeClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithTag("empty_state")
            .assertIsDisplayed()
    }

    @Test
    fun displaysRecipeList() {
        composeTestRule.setContent {
            RecipesAppTheme {
                RecipesContent(
                    uiState = RecipesUiState(
                        recipes = listOf(
                            RecipeUiModel(
                                id = 1,
                                title = "Классический бургер",
                                imageUrl = "",
                                ingredients = listOf(
                                    IngredientUiModel(
                                        name = "булочка",
                                        quantity = "1",
                                        unitOfMeasure = "шт"
                                    )
                                ),
                                method = listOf("Собрать бургер"),
                                isFavorite = false
                            )
                        )
                    ),
                    onRecipeClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("КЛАССИЧЕСКИЙ БУРГЕР")
            .assertIsDisplayed()
    }
}