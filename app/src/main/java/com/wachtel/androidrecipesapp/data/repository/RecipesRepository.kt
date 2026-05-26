package com.wachtel.androidrecipesapp.data.repository

import com.wachtel.androidrecipesapp.data.model.CategoryDto
import com.wachtel.androidrecipesapp.data.model.RecipeDto
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {

    fun getCategories(): Flow<List<CategoryDto>>

    fun getRecipesByCategory(categoryId: Int): Flow<List<RecipeDto>>

    suspend fun getRecipe(recipeId: Int): RecipeDto
}