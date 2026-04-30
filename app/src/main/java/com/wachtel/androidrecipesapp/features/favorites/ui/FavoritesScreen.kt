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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.wachtel.androidrecipesapp.R
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.data.repository.RecipesRepositoryStub
import com.wachtel.androidrecipesapp.features.recipes.ui.RecipeItem
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.RecipeUiModel
import com.wachtel.androidrecipesapp.features.recipes.presentation.model.toUiModel
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import com.wachtel.androidrecipesapp.core.utils.FavoriteDataStoreManager
import kotlinx.coroutines.flow.map

@Composable
fun FavoritesScreen(
    recipesRepository: RecipesRepositoryStub,
    favoriteManager: FavoriteDataStoreManager,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteRecipesFlow = remember(recipesRepository, favoriteManager) {
        favoriteManager.getFavoriteIdsFlow().map { ids ->
            ids.mapNotNull { idString ->
                val recipeId = idString.toIntOrNull() ?: return@mapNotNull null

                recipesRepository
                    .getRecipeById(recipeId)
                    ?.toUiModel()
                    ?.copy(isFavorite = true)
            }
        }
    }

    val favoriteRecipes by favoriteRecipesFlow.collectAsState(
        initial = emptyList<RecipeUiModel>()
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenHeader(
            imagePainter = painterResource(id = R.drawable.bcg_favorites),
            contentDescription = "Избранное",
            title = "Избранное"
        )

        if (favoriteRecipes.isEmpty()) {
            EmptyFavoritesContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Space16)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(Dimens.Space16),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
            ) {
                items(
                    items = favoriteRecipes,
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