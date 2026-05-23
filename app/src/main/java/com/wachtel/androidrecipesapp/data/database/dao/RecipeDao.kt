package com.wachtel.androidrecipesapp.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.wachtel.androidrecipesapp.data.database.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<RecipeEntity>>
}