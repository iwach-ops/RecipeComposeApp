package com.wachtel.androidrecipesapp.features.recipes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.features.recipes.presentation.RecipesViewModel
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.IngredientUiModel
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipeUiModel
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipesUiState
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesScreen(
    modifier: Modifier = Modifier,
    onRecipeClick: (Int) -> Unit
) {
    val viewModel: RecipesViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    RecipesScreenContent(
        uiState = uiState,
        modifier = modifier,
        onRetryClick = viewModel::loadRecipes,
        onRecipeClick = onRecipeClick
    )
}

@Composable
private fun RecipesScreenContent(
    uiState: RecipesUiState,
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit,
    onRecipeClick: (Int) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenHeader(
            imagePainter = rememberAsyncImagePainter(
                model = uiState.categoryImageUrl,
                placeholder = ColorPainter(Color(0xFFDCE8FF)),
                error = ColorPainter(Color(0xFFDCE8FF))
            ),
            contentDescription = uiState.categoryTitle,
            title = uiState.categoryTitle
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(Dimens.Space16),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )

                        Button(onClick = onRetryClick) {
                            Text(text = "Повторить")
                        }
                    }
                }
            }

            uiState.isEmpty -> {
                EmptyRecipesContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(Dimens.Space16)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(Dimens.Space16),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
                ) {
                    items(
                        items = uiState.recipes,
                        key = { recipe -> recipe.id }
                    ) { recipe ->
                        RecipeItem(
                            recipe = recipe,
                            onClick = {
                                onRecipeClick(recipe.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRecipesContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(Dimens.CornerExtraLarge),
            tonalElevation = Dimens.CardElevation,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ) {
            Text(
                text = "Для этой категории пока нет рецептов",
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

@Preview(showBackground = true)
@Composable
private fun RecipesScreenPreview() {
    RecipesAppTheme {
        RecipesScreenContent(
            uiState = RecipesUiState(
                categoryTitle = "Бургеры",
                categoryImageUrl = "",
                recipes = listOf(
                    RecipeUiModel(
                        id = 1,
                        title = "Классический бургер",
                        imageUrl = "",
                        ingredients = listOf(
                            IngredientUiModel(
                                name = "булочка",
                                quantity = "1",
                                unitOfMeasure = "шт"
                            )
                        ),
                        method = listOf(
                            "Подготовьте ингредиенты",
                            "Соберите бургер"
                        ),
                        isFavorite = false
                    )
                ),
                isLoading = false,
                errorMessage = null
            ),
            onRetryClick = {},
            onRecipeClick = {}
        )
    }
}