package com.wachtel.androidrecipesapp.features.favorites.ui

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wachtel.androidrecipesapp.R
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.features.favorites.presentation.FavoritesViewModel
import com.wachtel.androidrecipesapp.features.favorites.presentation.model.FavoritesUiState
import com.wachtel.androidrecipesapp.features.recipes.ui.RecipeItem
import com.wachtel.androidrecipesapp.ui.theme.Dimens

@Composable
fun FavoritesScreen(
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    FavoritesScreenContent(
        uiState = uiState,
        onRecipeClick = onRecipeClick,
        modifier = modifier
    )
}

@Composable
private fun FavoritesScreenContent(
    uiState: FavoritesUiState,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenHeader(
            imagePainter = painterResource(id = R.drawable.bcg_favorites),
            contentDescription = "Избранное",
            title = "Избранное"
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
                FavoritesErrorContent(
                    errorMessage = uiState.errorMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(Dimens.Space16)
                )
            }

            uiState.isEmpty -> {
                EmptyFavoritesContent(
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
private fun FavoritesErrorContent(
    errorMessage: String,
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
                text = errorMessage,
                modifier = Modifier.padding(
                    horizontal = Dimens.Space20,
                    vertical = Dimens.Space24
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun EmptyFavoritesContent(
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
                text = "У вас пока нет избранных рецептов. Добавьте рецепт в избранное на экране деталей.",
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