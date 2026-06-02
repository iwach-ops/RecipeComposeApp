package com.wachtel.androidrecipesapp.core.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.flow.first

@RunWith(AndroidJUnit4::class)
class FavoritesDataStoreTest {

    private lateinit var context: Context
    private lateinit var manager: FavoriteDataStoreManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        manager = FavoriteDataStoreManager(context)

        runBlocking {
            context.appDataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            context.appDataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }

    @Test
    fun addFavoriteSavesRecipeId() = runTest {
        manager.addFavorite(recipeId = 42)

        val favorites = manager.getFavoriteIdsFlow().first()

        assertTrue(favorites.contains("42"))
    }

    @Test
    fun removeFromFavoritesDeletesRecipeId() = runTest {
        manager.addFavorite(recipeId = 42)
        manager.removeFavorite(recipeId = 42)

        val favorites = manager.getFavoriteIdsFlow().first()

        assertFalse(favorites.contains("42"))
    }

    @Test
    fun favoritesFlowEmitsUpdatesReactively() = runTest {
        manager.getFavoriteIdsFlow().test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())

            manager.addFavorite(recipeId = 42)

            val updated = awaitItem()
            assertTrue(updated.contains("42"))

            cancelAndIgnoreRemainingEvents()
        }
    }
}