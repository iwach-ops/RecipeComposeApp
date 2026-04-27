package com.wachtel.androidrecipesapp.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

class FavoriteDataStoreManager(
    private val context: Context
) {

    suspend fun isFavorite(recipeId: Int): Boolean {
        val favorites = context.appDataStore.data
            .map { prefs ->
                prefs[PreferencesKeys.FAVORITE_RECIPE_IDS].orEmpty()
            }
            .first()

        return favorites.contains(recipeId.toString())
    }

    suspend fun addFavorite(recipeId: Int) {
        context.appDataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.FAVORITE_RECIPE_IDS]
                .orEmpty()
                .toMutableSet()

            current.add(recipeId.toString())
            prefs[PreferencesKeys.FAVORITE_RECIPE_IDS] = current
        }
    }

    suspend fun removeFavorite(recipeId: Int) {
        context.appDataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.FAVORITE_RECIPE_IDS]
                .orEmpty()
                .toMutableSet()

            current.remove(recipeId.toString())
            prefs[PreferencesKeys.FAVORITE_RECIPE_IDS] = current
        }
    }

    fun getFavoriteIdsFlow(): Flow<Set<String>> {
        return context.appDataStore.data.map { prefs ->
            prefs[PreferencesKeys.FAVORITE_RECIPE_IDS].orEmpty()
        }
    }

    fun isFavoriteFlow(recipeId: Int): Flow<Boolean> {
        return getFavoriteIdsFlow().map { favoriteIds ->
            favoriteIds.contains(recipeId.toString())
        }
    }

    fun getFavoriteCountFlow(): Flow<Int> {
        return getFavoriteIdsFlow().map { favoriteIds ->
            favoriteIds.size
        }
    }
}