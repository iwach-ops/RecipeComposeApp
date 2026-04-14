package com.wachtel.androidrecipesapp.core

const val ASSETS_URI_PREFIX = "file:///android_asset/"

const val PARAM_RECIPE_ID = "recipeId"
const val RECIPE_ROUTE = "recipe/{$PARAM_RECIPE_ID}"

const val RECIPE_CUSTOM_SCHEME = "recipeapp"
const val RECIPE_CUSTOM_HOST = "recipe"

const val RECIPE_HTTPS_SCHEME = "https"
const val RECIPE_HOST = "recipes.androidsprint.ru"
const val RECIPE_PATH = "recipe"

fun createRecipeDeepLink(recipeId: Int): String {
    return "$RECIPE_HTTPS_SCHEME://$RECIPE_HOST/$RECIPE_PATH/$recipeId"
}