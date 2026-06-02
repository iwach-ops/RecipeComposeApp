package com.wachtel.androidrecipesapp.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.wachtel.androidrecipesapp.core.network.api.RecipesApiService
import com.wachtel.androidrecipesapp.data.database.RecipesDatabase
import com.wachtel.androidrecipesapp.data.database.dao.CategoryDao
import com.wachtel.androidrecipesapp.data.database.entity.CategoryEntity
import com.wachtel.androidrecipesapp.data.model.CategoryDto
import com.wachtel.androidrecipesapp.data.model.RecipeDto
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipesRepositoryIntegrationTest {

    private lateinit var database: RecipesDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var apiService: FakeRecipesApiService
    private lateinit var repository: RecipesRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            RecipesDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        categoryDao = database.categoryDao()
        apiService = FakeRecipesApiService()

        repository = RecipesRepositoryImpl(
            apiService = apiService,
            database = database
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun savesDataToCacheAfterSuccessfulApiCall() = runTest {
        apiService.categoriesResult = {
            listOf(
                CategoryDto(
                    id = 1,
                    title = "Завтраки",
                    description = "Лёгкие блюда",
                    imageUrl = "breakfast.jpg"
                )
            )
        }

        repository.getCategories().test {
            val firstEmission = awaitItem()

            val loaded = if (firstEmission.isEmpty()) {
                awaitItem()
            } else {
                firstEmission
            }

            assertEquals(1, loaded.size)
            assertEquals("Завтраки", loaded[0].title)
            assertEquals("Лёгкие блюда", loaded[0].description)
            assertEquals("breakfast.jpg", loaded[0].imageUrl)

            cancelAndIgnoreRemainingEvents()
        }

        val cached = categoryDao
            .getAllCategories()
            .first { categories -> categories.isNotEmpty() }

        assertEquals(1, cached.size)
        assertEquals("Завтраки", cached[0].name)
        assertEquals("Лёгкие блюда", cached[0].description)
        assertEquals("breakfast.jpg", cached[0].imageUrl)
    }

    @Test
    fun returnsCachedDataWhenApiFails() = runTest {
        categoryDao.insertCategories(
            listOf(
                CategoryEntity(
                    id = 1,
                    name = "Кешированные завтраки",
                    description = "Из Room",
                    imageUrl = "cached.jpg"
                )
            )
        )

        apiService.categoriesResult = {
            throw IOException("Network error")
        }

        repository.getCategories().test {
            val cached = awaitItem()

            assertEquals(1, cached.size)
            assertEquals("Кешированные завтраки", cached[0].title)
            assertEquals("Из Room", cached[0].description)
            assertEquals("cached.jpg", cached[0].imageUrl)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private class FakeRecipesApiService : RecipesApiService {

        var categoriesResult: suspend () -> List<CategoryDto> = {
            emptyList()
        }

        override suspend fun getCategories(): List<CategoryDto> {
            return categoriesResult()
        }

        override suspend fun getRecipesByCategory(categoryId: Int): List<RecipeDto> {
            return emptyList()
        }

        override suspend fun getRecipe(recipeId: Int): RecipeDto {
            error("Not used in this test")
        }
    }
}