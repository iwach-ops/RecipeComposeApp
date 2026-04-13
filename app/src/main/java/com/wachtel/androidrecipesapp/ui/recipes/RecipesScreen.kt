package com.wachtel.androidrecipesapp.ui.recipes

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.data.repository.RecipesRepositoryStub
import com.wachtel.androidrecipesapp.ui.recipes.model.RecipeUiModel
import com.wachtel.androidrecipesapp.ui.recipes.model.toUiModel
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesScreen(
    categoryId: Int,
    categoryTitle: String,
    modifier: Modifier = Modifier,
    onRecipeClick: (Int, RecipeUiModel) -> Unit
) {
    var recipes by remember {
        mutableStateOf<List<RecipeUiModel>>(emptyList())
    }

    LaunchedEffect(categoryId) {
        recipes = RecipesRepositoryStub
            .getRecipesByCategoryId(categoryId)
            .map { it.toUiModel() }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenHeader(
            imagePainter = ColorPainter(Color(0xFFDCE8FF)),
            contentDescription = categoryTitle,
            title = categoryTitle
        )

        if (recipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.Space16),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(Dimens.CornerExtraLarge),
                    tonalElevation = Dimens.CardElevation
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(Dimens.Space16),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
            ) {
                items(
                    items = recipes,
                    key = { it.id }
                ) { recipe ->
                    RecipeItem(
                        recipe = recipe,
                        onClick = {
                            onRecipeClick(recipe.id, recipe)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecipesScreenPreview() {
    RecipesAppTheme {
        RecipesScreen(
            categoryId = 0,
            categoryTitle = "Бургеры",
            onRecipeClick = { _, _ -> }
        )
    }
}