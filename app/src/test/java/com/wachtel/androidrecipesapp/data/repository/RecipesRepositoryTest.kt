package com.wachtel.androidrecipesapp.data.repository

import app.cash.turbine.test
import com.wachtel.androidrecipesapp.core.network.api.RecipesApiService
import com.wachtel.androidrecipesapp.data.database.RecipesDatabase
import com.wachtel.androidrecipesapp.data.database.dao.CategoryDao
import com.wachtel.androidrecipesapp.data.database.dao.RecipeDao
import com.wachtel.androidrecipesapp.data.database.entity.CategoryEntity
import com.wachtel.androidrecipesapp.data.database.entity.RecipeEntity
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RecipesRepositoryTest {

    private val apiService = mockk<RecipesApiService>()
    private val database = mockk<RecipesDatabase>(relaxed = true)
    private val categoryDao = mockk<CategoryDao>()
    private val recipeDao = mockk<RecipeDao>()

    private lateinit var repository: RecipesRepositoryImpl

    @Before
    fun setup() {
        every { database.categoryDao() } returns categoryDao
        every { database.recipeDao() } returns recipeDao

        repository = RecipesRepositoryImpl(
            apiService = apiService,
            database = database
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getCategories emits categories from database`() = runTest {
        // Arrange
        val cachedCategories = listOf(
            CategoryEntity(
                id = 1,
                name = "Завтраки",
                description = "Утренние блюда",
                imageUrl = "breakfast.jpg"
            )
        )

        every { categoryDao.getAllCategories() } returns flowOf(cachedCategories)
        coEvery { apiService.getCategories() } returns emptyList()
        coEvery { categoryDao.insertCategories(any()) } just Runs

        // Act + Assert
        repository.getCategories().test {
            val categories = awaitItem()

            assertEquals(1, categories.size)
            assertEquals(1, categories[0].id)
            assertEquals("Завтраки", categories[0].title)
            assertEquals("Утренние блюда", categories[0].description)
            assertEquals("breakfast.jpg", categories[0].imageUrl)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(timeout = 1_000) {
            apiService.getCategories()
        }

        coVerify(timeout = 1_000) {
            categoryDao.insertCategories(any())
        }
    }

    @Test
    fun `getCategories still emits data when api throws exception`() = runTest {
        // Arrange
        val cachedCategories = listOf(
            CategoryEntity(
                id = 2,
                name = "Десерты",
                description = "Сладкие блюда",
                imageUrl = "dessert.jpg"
            )
        )

        every { categoryDao.getAllCategories() } returns flowOf(cachedCategories)
        coEvery { apiService.getCategories() } throws RuntimeException("Network error")

        // Act + Assert
        repository.getCategories().test {
            val categories = awaitItem()

            assertEquals(1, categories.size)
            assertEquals(2, categories[0].id)
            assertEquals("Десерты", categories[0].title)
            assertEquals("Сладкие блюда", categories[0].description)
            assertEquals("dessert.jpg", categories[0].imageUrl)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(timeout = 1_000) {
            apiService.getCategories()
        }

        coVerify(exactly = 0) {
            categoryDao.insertCategories(any())
        }
    }

    @Test
    fun `getRecipesByCategory returns flow filtered by categoryId`() = runTest {
        // Arrange
        val categoryId = 10

        val cachedRecipes = listOf(
            RecipeEntity(
                id = 1,
                title = "Pasta Carbonara",
                categoryId = categoryId,
                imageUrl = "pasta.jpg",
                ingredients = "200:::г:::Паста",
                method = "Отварить пасту|||Смешать ингредиенты"
            )
        )

        every {
            recipeDao.getRecipesByCategoryId(categoryId)
        } returns flowOf(cachedRecipes)

        coEvery {
            apiService.getRecipesByCategory(categoryId)
        } returns emptyList()

        coEvery {
            recipeDao.insertRecipes(any())
        } just Runs

        // Act + Assert
        repository.getRecipesByCategory(categoryId).test {
            val recipes = awaitItem()

            assertEquals(1, recipes.size)
            assertEquals(1, recipes[0].id)
            assertEquals("Pasta Carbonara", recipes[0].title)
            assertEquals("pasta.jpg", recipes[0].imageUrl)
            assertEquals(1, recipes[0].ingredients.size)
            assertEquals("Паста", recipes[0].ingredients[0].description)
            assertEquals("200", recipes[0].ingredients[0].quantity)
            assertEquals("г", recipes[0].ingredients[0].unitOfMeasure)
            assertEquals(
                listOf("Отварить пасту", "Смешать ингредиенты"),
                recipes[0].method
            )

            cancelAndIgnoreRemainingEvents()
        }

        verify {
            recipeDao.getRecipesByCategoryId(categoryId)
        }

        coVerify(timeout = 1_000) {
            apiService.getRecipesByCategory(categoryId)
        }

        coVerify(timeout = 1_000) {
            recipeDao.insertRecipes(any())
        }
    }
}