package com.wachtel.androidrecipesapp.util

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.stringSetPreferencesKey

private const val DATASTORE_NAME = "recipe_app_prefs"

private const val LEGACY_PREFS_NAME = "favorite_prefs"

val Context.appDataStore by preferencesDataStore(
    name = DATASTORE_NAME,
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = LEGACY_PREFS_NAME
            )
        )
    }
)

object PreferencesKeys {
    val FAVORITE_RECIPE_IDS = stringSetPreferencesKey("favorite_recipe_ids")
}