package com.wachtel.androidrecipesapp.core

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

const val PARAM_CATEGORY_ID = "categoryId"
const val PARAM_CATEGORY_TITLE = "categoryTitle"
const val PARAM_CATEGORY_IMAGE_URL = "categoryImageUrl"
const val PARAM_RECIPE_ID = "recipeId"

const val ASSETS_URI_PREFIX = "file:///android_asset/"

const val RECIPES_BASE_ROUTE = "recipes"
const val RECIPE_BASE_ROUTE = "recipe"

const val DEFAULT_CATEGORY_ID = -1
const val DEFAULT_CATEGORY_TITLE = "Рецепты"
const val DEFAULT_CATEGORY_IMAGE_URL = ""

const val RECIPE_CUSTOM_SCHEME = "androidrecipes"
const val RECIPE_CUSTOM_HOST = "recipe"
const val RECIPE_HOST = "www.androidrecipes.com"
const val RECIPE_PATH = "recipe"

const val RECIPES_ROUTE =
    "$RECIPES_BASE_ROUTE?$PARAM_CATEGORY_ID={$PARAM_CATEGORY_ID}" +
            "&$PARAM_CATEGORY_TITLE={$PARAM_CATEGORY_TITLE}" +
            "&$PARAM_CATEGORY_IMAGE_URL={$PARAM_CATEGORY_IMAGE_URL}"

const val RECIPE_ROUTE = "$RECIPE_BASE_ROUTE/{$PARAM_RECIPE_ID}"

const val RECIPES_DEEP_LINK_PATTERN =
    "$RECIPE_CUSTOM_SCHEME://$RECIPES_BASE_ROUTE?$PARAM_CATEGORY_ID={$PARAM_CATEGORY_ID}" +
            "&$PARAM_CATEGORY_TITLE={$PARAM_CATEGORY_TITLE}" +
            "&$PARAM_CATEGORY_IMAGE_URL={$PARAM_CATEGORY_IMAGE_URL}"

fun createRecipesRoute(
    categoryId: Int = DEFAULT_CATEGORY_ID,
    categoryTitle: String = DEFAULT_CATEGORY_TITLE,
    categoryImageUrl: String = DEFAULT_CATEGORY_IMAGE_URL
): String {
    val charsetName = StandardCharsets.UTF_8.name()
    val encodedCategoryTitle = URLEncoder.encode(categoryTitle, charsetName)
    val encodedCategoryImageUrl = URLEncoder.encode(categoryImageUrl, charsetName)

    return "$RECIPES_BASE_ROUTE?" +
            "$PARAM_CATEGORY_ID=$categoryId&" +
            "$PARAM_CATEGORY_TITLE=$encodedCategoryTitle&" +
            "$PARAM_CATEGORY_IMAGE_URL=$encodedCategoryImageUrl"
}

fun createRecipeDeepLink(recipeId: Int): String {
    return "$RECIPE_CUSTOM_SCHEME://$RECIPE_CUSTOM_HOST/$recipeId"
}
