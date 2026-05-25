package com.wachtel.androidrecipesapp.data.model

import com.wachtel.androidrecipesapp.data.database.converter.Converters
import com.wachtel.androidrecipesapp.data.database.entity.RecipeEntity
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    val id: Int,
    val title: String,
    val ingredients: List<IngredientDto>,
    val method: List<String>,
    val imageUrl: String
)

fun RecipeDto.toEntity(categoryId: Int): RecipeEntity {
    val converters = Converters()

    return RecipeEntity(
        id = id,
        title = title,
        categoryId = categoryId,
        imageUrl = imageUrl,
        ingredients = converters.fromList(
            ingredients.map { ingredient ->
                ingredient.toStorageString()
            }
        ),
        method = converters.fromList(method)
    )
}

fun RecipeEntity.toDto(): RecipeDto {
    val converters = Converters()

    return RecipeDto(
        id = id,
        title = title,
        ingredients = converters
            .fromString(ingredients)
            .mapNotNull { ingredientString ->
                ingredientString.toIngredientDtoOrNull()
            },
        method = converters.fromString(method),
        imageUrl = imageUrl
    )
}

private fun IngredientDto.toStorageString(): String {
    return listOf(
        quantity,
        unitOfMeasure,
        description
    ).joinToString(INGREDIENT_FIELD_SEPARATOR)
}

private fun String.toIngredientDtoOrNull(): IngredientDto? {
    val parts = split(
        INGREDIENT_FIELD_SEPARATOR,
        limit = INGREDIENT_PARTS_COUNT
    )

    if (parts.size != INGREDIENT_PARTS_COUNT) {
        return null
    }

    return IngredientDto(
        quantity = parts[0],
        unitOfMeasure = parts[1],
        description = parts[2]
    )
}

private const val INGREDIENT_FIELD_SEPARATOR = ":::"
private const val INGREDIENT_PARTS_COUNT = 3