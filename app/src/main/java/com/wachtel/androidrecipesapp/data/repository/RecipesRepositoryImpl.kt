package com.wachtel.androidrecipesapp.data.repository

import android.util.Log
import com.wachtel.androidrecipesapp.core.network.api.RecipesApiService
import com.wachtel.androidrecipesapp.data.model.CategoryDto
import com.wachtel.androidrecipesapp.data.model.RecipeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipesRepositoryImpl(
    private val apiService: RecipesApiService
) : RecipesRepository {

    override suspend fun getCategories(): List<CategoryDto> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getCategories()
            } catch (exception: Exception) {
                Log.e(TAG, "Ошибка при загрузке категорий", exception)
                emptyList()
            }
        }
    }

    override suspend fun getRecipesByCategory(categoryId: Int): List<RecipeDto> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getRecipesByCategory(categoryId)
            } catch (exception: Exception) {
                Log.e(TAG, "Ошибка при загрузке рецептов категории: $categoryId", exception)
                emptyList()
            }
        }
    }

    override suspend fun getRecipe(recipeId: Int): RecipeDto {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getRecipe(recipeId)
            } catch (exception: Exception) {
                Log.e(TAG, "Ошибка при загрузке рецепта: $recipeId", exception)
                throw exception
            }
        }
    }

    companion object {
        private const val TAG = "RecipesRepository"
    }
}