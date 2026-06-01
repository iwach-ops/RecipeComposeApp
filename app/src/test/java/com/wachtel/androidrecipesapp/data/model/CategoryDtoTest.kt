package com.wachtel.androidrecipesapp.data.model

import com.wachtel.androidrecipesapp.core.IMAGES_BASE_URL
import com.wachtel.androidrecipesapp.features.categories.presentation.model.toUiModel
import com.wachtel.androidrecipesapp.fixtures.CategoryTestFixtures
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryDtoTest {

    @Test
    fun `converts DTO to UI model`() {
        // Arrange
        val dto = CategoryDto(
            id = 1,
            title = "Завтраки",
            description = "Утренние блюда",
            imageUrl = "breakfast.jpg"
        )

        // Act
        val result = dto.toUiModel()

        // Assert
        assertEquals(1, result.id)
        assertEquals("Завтраки", result.title)
        assertEquals("Утренние блюда", result.description)
        assertEquals("${IMAGES_BASE_URL}breakfast.jpg", result.imageUrl)
    }

    @Test
    fun `mapper maps empty title correctly`() {
        // Arrange
        val dto = CategoryTestFixtures.createCategoryDto(
            title = ""
        )

        // Act
        val result = dto.toUiModel()

        // Assert
        assertEquals("", result.title)
        assertEquals(dto.id, result.id)
        assertEquals(dto.description, result.description)
    }

    @Test
    fun `mapper preserves very long description`() {
        // Arrange
        val longDescription = "Очень длинное описание категории. ".repeat(50)

        val dto = CategoryTestFixtures.createCategoryDto(
            description = longDescription
        )

        // Act
        val result = dto.toUiModel()

        // Assert
        assertEquals(longDescription, result.description)
    }
}