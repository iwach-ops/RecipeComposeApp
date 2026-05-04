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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.core.utils.shareRecipe
import com.wachtel.androidrecipesapp.features.details.presentation.RecipeDetailsViewModel
import com.wachtel.androidrecipesapp.features.details.presentation.model.RecipeDetailsUiState
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import kotlin.math.roundToInt

@Composable
fun RecipeDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            RecipeDetailsLoadingContent(
                modifier = modifier
            )
        }

        uiState.recipe == null && uiState.errorMessage != null -> {
            RecipeDetailsErrorContent(
                errorMessage = uiState.errorMessage.orEmpty(),
                onRetryClick = viewModel::loadRecipe,
                modifier = modifier
            )
        }

        uiState.recipe == null -> {
            RecipeNotFoundScreen(
                modifier = modifier
            )
        }

        else -> {
            RecipeDetailsContent(
                uiState = uiState,
                onFavoriteClick = viewModel::toggleFavorite,
                onPortionsChange = viewModel::updatePortions,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun RecipeDetailsContent(
    uiState: RecipeDetailsUiState,
    onFavoriteClick: () -> Unit,
    onPortionsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val recipe = uiState.recipe ?: return

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
            isFavorite = uiState.isFavorite,
            onFavoriteClick = onFavoriteClick
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
                            text = "${uiState.portions}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Slider(
                            value = uiState.portions.toFloat(),
                            onValueChange = { value ->
                                onPortionsChange(value.roundToInt())
                            },
                            valueRange = RecipeDetailsUiState.MIN_PORTIONS.toFloat()..
                                    RecipeDetailsUiState.MAX_PORTIONS.toFloat(),
                            steps = RecipeDetailsUiState.MAX_PORTIONS -
                                    RecipeDetailsUiState.MIN_PORTIONS - 1,
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
                        uiState.recalculatedIngredients.forEachIndexed { index, ingredient ->
                            IngredientItem(
                                ingredient = ingredient,
                                modifier = Modifier.padding(vertical = Dimens.Space12)
                            )

                            if (index < uiState.recalculatedIngredients.lastIndex) {
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
private fun RecipeDetailsLoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun RecipeDetailsErrorContent(
    errorMessage: String,
    onRetryClick: () -> Unit,
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
            Column(
                modifier = Modifier.padding(
                    horizontal = Dimens.Space20,
                    vertical = Dimens.Space24
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )

                Button(
                    onClick = onRetryClick
                ) {
                    Text(text = "Повторить")
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