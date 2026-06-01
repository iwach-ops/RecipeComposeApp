package com.wachtel.androidrecipesapp.data.model

import com.wachtel.androidrecipesapp.core.IMAGES_BASE_URL
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.toUiModel
import com.wachtel.androidrecipesapp.fixtures.RecipeTestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RecipeDtoMapperTest {

    @Test
    fun `maps DTO to UI model correctly`() {
        // Arrange
        val dto = RecipeTestFixtures.createRecipeDto(
            id = 1,
            title = "Pasta Carbonara",
            ingredients = listOf(
                RecipeTestFixtures.createIngredientDto(
                    quantity = "200",
                    unitOfMeasure = "г",
                    description = "Паста"
                )
            ),
            method = listOf(
                "Отварить пасту",
                "Смешать ингредиенты"
            ),
            imageUrl = "pasta.jpg"
        )

        // Act
        val result = dto.toUiModel()

        // Assert
        assertEquals(1, result.id)
        assertEquals("Pasta Carbonara", result.title)
        assertEquals("${IMAGES_BASE_URL}pasta.jpg", result.imageUrl)
        assertEquals(1, result.ingredients.size)
        assertEquals("Паста", result.ingredients[0].name)
        assertEquals("200", result.ingredients[0].quantity)
        assertEquals("г", result.ingredients[0].unitOfMeasure)
        assertEquals(listOf("Отварить пасту", "Смешать ингредиенты"), result.method)
        assertFalse(result.isFavorite)
    }

    @Test
    fun `prepends base url to relative imageUrl`() {
        // Arrange
        val dto = RecipeTestFixtures.createRecipeDto(
            imageUrl = "pasta.jpg"
        )

        // Act
        val result = dto.toUiModel()

        // Assert
        assertEquals("${IMAGES_BASE_URL}pasta.jpg", result.imageUrl)
    }

    @Test
    fun `preserves full imageUrl starting with http`() {
        // Arrange
        val fullImageUrl = "https://example.com/images/pasta.jpg"

        val dto = RecipeTestFixtures.createRecipeDto(
            imageUrl = fullImageUrl
        )

        // Act
        val result = dto.toUiModel()

        // Assert
        assertEquals(fullImageUrl, result.imageUrl)
    }
}