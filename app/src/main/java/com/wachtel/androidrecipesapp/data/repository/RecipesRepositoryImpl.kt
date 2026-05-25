package com.wachtel.androidrecipesapp.data.repository

import android.util.Log
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
import kotlinx.coroutines.withContext

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

    override suspend fun getRecipe(recipeId: Int): RecipeDto {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getRecipe(recipeId)
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Ошибка при загрузке рецепта из API: $recipeId",
                    exception
                )

                recipeDao
                    .getRecipeById(recipeId)
                    .first()
                    ?.toDto()
                    ?: throw exception
            }
        }
    }

    companion object {
        private const val TAG = "RecipesRepository"
    }
}