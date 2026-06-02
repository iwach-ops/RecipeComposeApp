package com.wachtel.androidrecipesapp.data.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wachtel.androidrecipesapp.data.database.RecipesDatabase
import com.wachtel.androidrecipesapp.data.database.entity.CategoryEntity
import com.wachtel.androidrecipesapp.data.database.entity.RecipeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipesDaoTest {

    private lateinit var database: RecipesDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var recipeDao: RecipeDao

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
        recipeDao = database.recipeDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertsAndRetrievesCategories() = runTest {
        val categories = listOf(
            CategoryEntity(
                id = 1,
                name = "Завтраки",
                description = "Лёгкие блюда",
                imageUrl = "breakfast.jpg"
            ),
            CategoryEntity(
                id = 2,
                name = "Обеды",
                description = "Основные блюда",
                imageUrl = "lunch.jpg"
            )
        )

        categoryDao.insertCategories(categories)

        val result = categoryDao.getAllCategories().first()

        assertEquals(2, result.size)
    }

    @Test
    fun insertReplacesDuplicateCategory() = runTest {
        val originalCategory = CategoryEntity(
            id = 1,
            name = "Завтраки",
            description = "Старое описание",
            imageUrl = "old.jpg"
        )

        val updatedCategory = CategoryEntity(
            id = 1,
            name = "Новые завтраки",
            description = "Новое описание",
            imageUrl = "new.jpg"
        )

        categoryDao.insertCategories(listOf(originalCategory))
        categoryDao.insertCategories(listOf(updatedCategory))

        val result = categoryDao.getAllCategories().first()

        assertEquals(1, result.size)
        assertEquals("Новые завтраки", result[0].name)
        assertEquals("Новое описание", result[0].description)
        assertEquals("new.jpg", result[0].imageUrl)
    }

    @Test
    fun getRecipesByCategoryReturnsCorrectItems() = runTest {
        val recipes = listOf(
            RecipeEntity(
                id = 1,
                title = "Recipe 1",
                categoryId = 1,
                imageUrl = "recipe_1.jpg",
                ingredients = "1:::шт:::Ингредиент 1",
                method = "Шаг 1"
            ),
            RecipeEntity(
                id = 2,
                title = "Recipe 2",
                categoryId = 1,
                imageUrl = "recipe_2.jpg",
                ingredients = "1:::шт:::Ингредиент 2",
                method = "Шаг 1"
            ),
            RecipeEntity(
                id = 3,
                title = "Recipe 3",
                categoryId = 2,
                imageUrl = "recipe_3.jpg",
                ingredients = "1:::шт:::Ингредиент 3",
                method = "Шаг 1"
            )
        )

        recipeDao.insertRecipes(recipes)

        val result = recipeDao.getRecipesByCategoryId(categoryId = 1).first()

        assertEquals(2, result.size)
        assertTrue(result.all { recipe -> recipe.categoryId == 1 })
    }

    @Test
    fun emptyDatabaseReturnsEmptyList() = runTest {
        val result = categoryDao.getAllCategories().first()

        assertTrue(result.isEmpty())
    }
}