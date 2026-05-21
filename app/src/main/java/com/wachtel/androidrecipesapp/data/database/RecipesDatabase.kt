package com.wachtel.androidrecipesapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wachtel.androidrecipesapp.data.database.dao.CategoryDao
import com.wachtel.androidrecipesapp.data.database.entity.CategoryEntity

@Database(
    entities = [CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao

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