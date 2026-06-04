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
import com.wachtel.androidrecipesapp.R
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.features.categories.presentation.CategoriesViewModel
import com.wachtel.androidrecipesapp.features.categories.presentation.model.CategoriesUiState
import com.wachtel.androidrecipesapp.features.categories.presentation.model.CategoryUiModel
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.testTag

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (Int, String, String) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CategoriesContent(
        uiState = uiState,
        onCategoryClick = onCategoryClick,
        modifier = modifier.testTag("categories_screen"),
        onRetryClick = viewModel::loadCategories
    )
}
@Composable
fun CategoriesContent(
    uiState: CategoriesUiState,
    onCategoryClick: (Int, String, String) -> Unit,
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit = {}
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
                    CircularProgressIndicator(
                        modifier = Modifier.testTag("loading_indicator")
                    )
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
                            modifier = Modifier.testTag("error_message"),
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
                        .weight(1f)
                        .testTag("categories_grid"),
                    contentPadding = PaddingValues(Dimens.Space16),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.Space12),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Space12)
                ) {
                    items(
                        items = uiState.categories,
                        key = { category: CategoryUiModel -> category.id }
                    ) { category: CategoryUiModel ->
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
        CategoriesContent(
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