package com.wachtel.androidrecipesapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wachtel.androidrecipesapp.data.database.converter.Converters
import com.wachtel.androidrecipesapp.data.database.dao.CategoryDao
import com.wachtel.androidrecipesapp.data.database.dao.RecipeDao
import com.wachtel.androidrecipesapp.data.database.entity.CategoryEntity
import com.wachtel.androidrecipesapp.data.database.entity.RecipeEntity

@Database(
    entities = [
        CategoryEntity::class,
        RecipeEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

    abstract fun recipeDao(): RecipeDao

    companion object {
        private const val DATABASE_NAME = "recipes_database"

        fun buildDatabase(context: Context): RecipesDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                RecipesDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}