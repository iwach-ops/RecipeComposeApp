package com.wachtel.androidrecipesapp.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.data.repository.RecipesRepositoryStub
import com.wachtel.androidrecipesapp.ui.recipes.model.toUiModel
import com.wachtel.androidrecipesapp.ui.theme.Dimens

@Composable
fun RecipeDetailsScreen(
    recipeId: Int,
    modifier: Modifier = Modifier
) {
    val recipe = remember(recipeId) {
        RecipesRepositoryStub.getRecipeById(recipeId)?.toUiModel()
    }

    if (recipe == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(Dimens.Space16),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(Dimens.CornerExtraLarge),
                tonalElevation = Dimens.CardElevation
            ) {
                Text(
                    text = "Рецепт не найден",
                    modifier = Modifier.padding(
                        horizontal = Dimens.Space20,
                        vertical = Dimens.Space24
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            imagePainter = rememberAsyncImagePainter(model = recipe.imageUrl),
            contentDescription = recipe.title,
            title = recipe.title
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Space16),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space24)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
            ) {
                Text(
                    text = "Ингредиенты",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.CornerLarge),
                    tonalElevation = Dimens.CardElevation,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = Dimens.Space16,
                            vertical = Dimens.Space8
                        )
                    ) {
                        recipe.ingredients.forEachIndexed { index, ingredient ->
                            IngredientItem(
                                ingredient = ingredient,
                                modifier = Modifier.padding(vertical = Dimens.Space12)
                            )

                            if (index < recipe.ingredients.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
            ) {
                Text(
                    text = "Способ приготовления",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                recipe.method.forEachIndexed { index, step ->
                    StepItem(
                        stepNumber = index + 1,
                        text = step.removeNumberPrefix()
                    )
                }
            }
        }
    }
}

@Composable
private fun StepItem(
    stepNumber: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space12),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.IconLarge)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun String.removeNumberPrefix(): String {
    return replace(Regex("^\\d+\\.\\s*"), "").trim()
}