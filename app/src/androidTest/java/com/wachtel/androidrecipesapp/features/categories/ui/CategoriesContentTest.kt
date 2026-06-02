package com.wachtel.androidrecipesapp.features.categories.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.wachtel.androidrecipesapp.features.categories.presentation.model.CategoriesUiState
import com.wachtel.androidrecipesapp.features.categories.presentation.model.CategoryUiModel
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CategoriesContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysCategories() {
        composeTestRule.setContent {
            RecipesAppTheme {
                CategoriesContent(
                    uiState = CategoriesUiState(
                        categories = listOf(
                            CategoryUiModel(
                                id = 1,
                                title = "Завтраки",
                                description = "Утренние блюда",
                                imageUrl = ""
                            )
                        )
                    ),
                    onCategoryClick = { _, _, _ -> }
                )
            }
        }

        composeTestRule
            .onNodeWithText("ЗАВТРАКИ")
            .assertIsDisplayed()
    }

    @Test
    fun clickingCategoryNavigatesToRecipes() {
        var clickedId: Int? = null

        composeTestRule.setContent {
            RecipesAppTheme {
                CategoriesContent(
                    uiState = CategoriesUiState(
                        categories = listOf(
                            CategoryUiModel(
                                id = 7,
                                title = "Завтраки",
                                description = "Утренние блюда",
                                imageUrl = ""
                            )
                        )
                    ),
                    onCategoryClick = { id, _, _ ->
                        clickedId = id
                    }
                )
            }
        }

        composeTestRule
            .onNodeWithText("ЗАВТРАКИ")
            .performClick()

        assertEquals(7, clickedId)
    }

    @Test
    fun showsLoadingState() {
        composeTestRule.setContent {
            RecipesAppTheme {
                CategoriesContent(
                    uiState = CategoriesUiState(
                        isLoading = true
                    ),
                    onCategoryClick = { _, _, _ -> }
                )
            }
        }

        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }
}