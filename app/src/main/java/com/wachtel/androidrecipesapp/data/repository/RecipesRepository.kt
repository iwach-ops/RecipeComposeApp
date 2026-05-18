package com.wachtel.androidrecipesapp.data.repository

import com.wachtel.androidrecipesapp.data.model.CategoryDto
import com.wachtel.androidrecipesapp.data.model.RecipeDto

interface RecipesRepository {

    suspend fun getCategories(): List<CategoryDto>

    suspend fun getRecipesByCategory(categoryId: Int): List<RecipeDto>

    suspend fun getRecipe(recipeId: Int): RecipeDto
}