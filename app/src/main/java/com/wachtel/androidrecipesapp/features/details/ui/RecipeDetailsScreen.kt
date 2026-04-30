package com.wachtel.androidrecipesapp.features.details.ui

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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.wachtel.androidrecipesapp.core.utils.shareRecipe
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipeUiModel
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.wachtel.androidrecipesapp.core.utils.FavoriteDataStoreManager
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

@Composable
fun RecipeDetailsScreen(
    recipe: RecipeUiModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val favoriteDataStoreManager = remember(context) {
        FavoriteDataStoreManager(context)
    }
    val coroutineScope = rememberCoroutineScope()

    val isFavorite by remember(recipe.id, favoriteDataStoreManager) {
        favoriteDataStoreManager.isFavoriteFlow(recipe.id)
    }.collectAsState(initial = false)

    var currentPortions by rememberSaveable(recipe.id) {
        mutableStateOf(1)
    }

    val recalculatedIngredients = remember(recipe.ingredients, currentPortions) {
        recipe.ingredients.map { ingredient ->
            ingredient.copy(
                quantity = ingredient.quantity.scaleToPortions(currentPortions)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            imagePainter = rememberAsyncImagePainter(model = recipe.imageUrl),
            contentDescription = recipe.title,
            title = recipe.title,
            showShareButton = true,
            onShareClick = {
                shareRecipe(
                    context = context,
                    recipeId = recipe.id,
                    recipeTitle = recipe.title
                )
            },
            showFavoriteButton = true,
            isFavorite = isFavorite,
            onFavoriteClick = {
                coroutineScope.launch {
                    if (isFavorite) {
                        favoriteDataStoreManager.removeFavorite(recipe.id)
                    } else {
                        favoriteDataStoreManager.addFavorite(recipe.id)
                    }
                }
            }
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
                    text = "Количество порций",
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
                        modifier = Modifier.padding(Dimens.Space16),
                        verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
                    ) {
                        Text(
                            text = "$currentPortions",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Slider(
                            value = currentPortions.toFloat(),
                            onValueChange = { value ->
                                currentPortions = value.roundToInt()
                            },
                            valueRange = 1f..8f,
                            steps = 6,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.tertiary,
                                activeTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
                                inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }

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
                        recalculatedIngredients.forEachIndexed { index, ingredient ->
                            IngredientItem(
                                ingredient = ingredient,
                                modifier = Modifier.padding(vertical = Dimens.Space12)
                            )

                            if (index < recalculatedIngredients.lastIndex) {
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

private fun String.scaleToPortions(portions: Int): String {
    val numericValue = replace(',', '.').toDoubleOrNull() ?: return this
    val scaledValue = numericValue * portions

    val formatted = if (scaledValue % 1.0 == 0.0) {
        scaledValue.toInt().toString()
    } else {
        String.format(Locale.US, "%.2f", scaledValue)
            .trimEnd('0')
            .trimEnd('.')
    }

    return formatted.replace('.', ',')
}

@Composable
fun RecipeNotFoundScreen(
    modifier: Modifier = Modifier
) {
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
}