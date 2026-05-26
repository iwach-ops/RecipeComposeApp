package com.wachtel.androidrecipesapp.data.model

import com.wachtel.androidrecipesapp.data.database.entity.CategoryEntity
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)

fun CategoryDto.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = title,
        description = description,
        imageUrl = imageUrl
    )
}

fun CategoryEntity.toDto(): CategoryDto {
    return CategoryDto(
        id = id,
        title = name,
        description = description,
        imageUrl = imageUrl
    )
}