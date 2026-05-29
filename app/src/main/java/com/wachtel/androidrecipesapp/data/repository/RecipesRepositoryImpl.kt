package com.wachtel.androidrecipesapp.data.repository

import android.util.Log
import com.wachtel.androidrecipesapp.core.DEFAULT_CATEGORY_ID
import com.wachtel.androidrecipesapp.core.network.api.RecipesApiService
import com.wachtel.androidrecipesapp.data.database.RecipesDatabase
import com.wachtel.androidrecipesapp.data.model.CategoryDto
import com.wachtel.androidrecipesapp.data.model.RecipeDto
import com.wachtel.androidrecipesapp.data.model.toDto
import com.wachtel.androidrecipesapp.data.model.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flowOf

class RecipesRepositoryImpl(
    private val apiService: RecipesApiService,
    database: RecipesDatabase
) : RecipesRepository {

    private val categoryDao = database.categoryDao()
    private val recipeDao = database.recipeDao()

    private val repositoryScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    override fun getCategories(): Flow<List<CategoryDto>> {
        repositoryScope.launch {
            try {
                val freshCategories = apiService.getCategories()

                categoryDao.insertCategories(
                    freshCategories.map { categoryDto ->
                        categoryDto.toEntity()
                    }
                )

                Log.d(
                    TAG,
                    "Категории обновлены из API: ${freshCategories.size}"
                )
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Ошибка при обновлении категорий из API",
                    exception
                )
            }
        }

        return categoryDao
            .getAllCategories()
            .map { entities ->
                entities.map { categoryEntity ->
                    categoryEntity.toDto()
                }
            }
    }

    override fun getRecipesByCategory(categoryId: Int): Flow<List<RecipeDto>> {
        repositoryScope.launch {
            try {
                val freshRecipes = apiService.getRecipesByCategory(categoryId)

                recipeDao.insertRecipes(
                    freshRecipes.map { recipeDto ->
                        recipeDto.toEntity(categoryId = categoryId)
                    }
                )

                Log.d(
                    TAG,
                    "Рецепты категории $categoryId обновлены из API: ${freshRecipes.size}"
                )
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Ошибка при обновлении рецептов категории: $categoryId",
                    exception
                )
            }
        }

        return recipeDao
            .getRecipesByCategoryId(categoryId)
            .map { entities ->
                entities.map { recipeEntity ->
                    recipeEntity.toDto()
                }
            }
    }

    override fun getRecipe(recipeId: Int): Flow<RecipeDto?> {
        repositoryScope.launch {
            try {
                val freshRecipe = apiService.getRecipe(recipeId)

                val categoryId = recipeDao
                    .getRecipeById(recipeId)
                    .first()
                    ?.categoryId
                    ?: DEFAULT_CATEGORY_ID

                recipeDao.insertRecipes(
                    listOf(
                        freshRecipe.toEntity(categoryId = categoryId)
                    )
                )

                Log.d(
                    TAG,
                    "Рецепт $recipeId обновлен из API"
                )
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Ошибка при обновлении рецепта: $recipeId",
                    exception
                )
            }
        }

        return recipeDao
            .getRecipeById(recipeId)
            .map { recipeEntity ->
                recipeEntity?.toDto()
            }
    }

    override fun getRecipesByIds(recipeIds: List<Int>): Flow<List<RecipeDto>> {
        if (recipeIds.isEmpty()) {
            return flowOf(emptyList())
        }
        return recipeDao
            .getRecipesByIds(recipeIds)
            .map { entities ->
                entities.map { recipeEntity ->
                    recipeEntity.toDto()
                }
            }
    }

    companion object {
        private const val TAG = "RecipesRepository"
    }
}