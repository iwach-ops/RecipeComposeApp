package com.wachtel.androidrecipesapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class FavoritePrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isFavorite(recipeId: Int): Boolean {
        return getAllFavorites().contains(recipeId.toString())
    }

    fun addToFavorites(recipeId: Int) {
        val updatedFavorites = getAllFavorites().toMutableSet().apply {
            add(recipeId.toString())
        }

        prefs.edit {
            putStringSet(FAVORITE_RECIPE_IDS_KEY, updatedFavorites)
        }
    }

    fun removeFromFavorites(recipeId: Int) {
        val updatedFavorites = getAllFavorites().toMutableSet().apply {
            remove(recipeId.toString())
        }

        prefs.edit {
            putStringSet(FAVORITE_RECIPE_IDS_KEY, updatedFavorites)
        }
    }

    fun getAllFavorites(): Set<String> {
        return prefs.getStringSet(FAVORITE_RECIPE_IDS_KEY, emptySet())
            ?.toSet() ?: emptySet()
    }

    companion object {
        private const val PREFS_NAME = "favorite_prefs"
        private const val FAVORITE_RECIPE_IDS_KEY = "favorite_recipe_ids"
    }
}