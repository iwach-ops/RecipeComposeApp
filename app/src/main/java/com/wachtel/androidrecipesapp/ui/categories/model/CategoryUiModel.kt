package com.wachtel.androidrecipesapp.ui.categories.model

import androidx.compose.runtime.Immutable
import com.wachtel.androidrecipesapp.core.ASSETS_URI_PREFIX
import com.wachtel.androidrecipesapp.data.model.CategoryDto

@Immutable
data class CategoryUiModel(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)

fun CategoryDto.toUiModel(): CategoryUiModel {
    return CategoryUiModel(
        id = id,
        title = title,
        description = description,
        imageUrl = if (imageUrl.startsWith("http", ignoreCase = true)) {
            imageUrl
        } else {
            ASSETS_URI_PREFIX + imageUrl
        }
    )
}