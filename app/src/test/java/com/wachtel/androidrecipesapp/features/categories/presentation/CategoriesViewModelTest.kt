package com.wachtel.androidrecipesapp.features.categories.presentation

import app.cash.turbine.test
import com.wachtel.androidrecipesapp.data.repository.RecipesRepository
import com.wachtel.androidrecipesapp.fixtures.CategoryTestFixtures
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

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
    fun `loads categories from repository`() = runTest {
        // Arrange
        val categories = CategoryTestFixtures.createCategoryDtoList(count = 2)
        every { repository.getCategories() } returns flowOf(categories)

        // Act
        val viewModel = CategoriesViewModel(repository)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()

            assertEquals(2, state.categories.size)
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)

            assertEquals(categories[0].id, state.categories[0].id)
            assertEquals(categories[0].title, state.categories[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shows empty list when repository returns no data`() = runTest {
        // Arrange
        every { repository.getCategories() } returns flowOf(emptyList())

        // Act
        val viewModel = CategoriesViewModel(repository)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()

            assertTrue(state.categories.isEmpty())
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shows error when repository throws`() = runTest {
        // Arrange
        every { repository.getCategories() } returns flow {
            throw IOException("Network error")
        }

        // Act
        val viewModel = CategoriesViewModel(repository)

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()

            assertTrue(state.categories.isEmpty())
            assertFalse(state.isLoading)
            assertNotNull(state.errorMessage)
            assertEquals("Network error", state.errorMessage)

            cancelAndIgnoreRemainingEvents()
        }
    }
}