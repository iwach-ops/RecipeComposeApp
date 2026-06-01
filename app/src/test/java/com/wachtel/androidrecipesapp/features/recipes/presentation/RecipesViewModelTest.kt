package com.wachtel.androidrecipesapp.features.recipes.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_ID
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_IMAGE_URL
import com.wachtel.androidrecipesapp.core.PARAM_CATEGORY_TITLE
import com.wachtel.androidrecipesapp.data.repository.RecipesRepository
import com.wachtel.androidrecipesapp.fixtures.RecipeTestFixtures
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipesViewModelTest {

    private val repository = mockk<RecipesRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `loads recipes for category`() = runTest {
        // Arrange
        val categoryId = 1
        val recipes = RecipeTestFixtures.createRecipeDtoList(count = 2)

        every {
            repository.getRecipesByCategory(categoryId)
        } returns flowOf(recipes)

        // Act
        val viewModel = createViewModel(
            categoryId = categoryId,
            categoryTitle = "Завтраки",
            categoryImageUrl = "breakfast.jpg"
        )

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(2, state.recipes.size)
            assertFalse(state.isLoading)
            assertEquals("Завтраки", state.categoryTitle)
            assertEquals("breakfast.jpg", state.categoryImageUrl)

            assertEquals(recipes[0].id, state.recipes[0].id)
            assertEquals(recipes[0].title, state.recipes[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state reflects category title from savedState`() = runTest {
        // Arrange
        val categoryId = 1

        every {
            repository.getRecipesByCategory(categoryId)
        } returns flowOf(emptyList())

        // Act
        val viewModel = createViewModel(
            categoryId = categoryId,
            categoryTitle = "Завтраки",
            categoryImageUrl = "breakfast.jpg"
        )

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals("Завтраки", state.categoryTitle)
            assertFalse(state.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shows error when repository throws`() = runTest {
        // Arrange
        val categoryId = 1

        every {
            repository.getRecipesByCategory(categoryId)
        } returns flow {
            throw IOException("Network error")
        }

        // Act
        val viewModel = createViewModel(
            categoryId = categoryId,
            categoryTitle = "Завтраки",
            categoryImageUrl = "breakfast.jpg"
        )

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()

            assertTrue(state.recipes.isEmpty())
            assertFalse(state.isLoading)
            assertNotNull(state.errorMessage)
            assertEquals("Network error", state.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel(
        categoryId: Int,
        categoryTitle: String,
        categoryImageUrl: String
    ): RecipesViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                PARAM_CATEGORY_ID to categoryId,
                PARAM_CATEGORY_TITLE to categoryTitle,
                PARAM_CATEGORY_IMAGE_URL to categoryImageUrl
            )
        )

        return RecipesViewModel(
            savedStateHandle = savedStateHandle,
            repository = repository
        )
    }
}