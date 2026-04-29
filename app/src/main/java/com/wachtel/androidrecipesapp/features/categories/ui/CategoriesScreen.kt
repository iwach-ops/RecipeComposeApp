package com.wachtel.androidrecipesapp.features.categories.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wachtel.androidrecipesapp.R
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.features.categories.presentation.CategoriesViewModel
import com.wachtel.androidrecipesapp.features.categories.presentation.model.CategoriesUiState
import com.wachtel.androidrecipesapp.features.categories.presentation.model.CategoryUiModel
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (Int, String, String) -> Unit
) {
    val viewModel: CategoriesViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    CategoriesScreenContent(
        uiState = uiState,
        modifier = modifier,
        onRetryClick = viewModel::loadCategories,
        onCategoryClick = onCategoryClick
    )
}

@Composable
private fun CategoriesScreenContent(
    uiState: CategoriesUiState,
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit,
    onCategoryClick: (Int, String, String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenHeader(
            imagePainter = painterResource(id = R.drawable.bcg_categories),
            contentDescription = uiState.title,
            title = uiState.title
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

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(Dimens.Space16),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space12),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
                ) {
                    items(
                        items = uiState.categories,
                        key = { it.id }
                    ) { category ->
                        CategoryItem(
                            category = category,
                            onClick = {
                                onCategoryClick(
                                    category.id,
                                    category.title,
                                    category.imageUrl
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriesScreenPreview() {
    RecipesAppTheme {
        CategoriesScreenContent(
            uiState = CategoriesUiState(
                categories = listOf(
                    CategoryUiModel(
                        id = 0,
                        title = "Бургеры",
                        description = "Рецепты всех популярных видов бургеров",
                        imageUrl = ""
                    ),
                    CategoryUiModel(
                        id = 1,
                        title = "Десерты",
                        description = "Самые вкусные рецепты десертов специально для вас",
                        imageUrl = ""
                    )
                )
            ),
            onRetryClick = {},
            onCategoryClick = { _, _, _ -> }
        )
    }
}