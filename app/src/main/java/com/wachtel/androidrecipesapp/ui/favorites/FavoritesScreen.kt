package com.wachtel.androidrecipesapp.ui.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.wachtel.androidrecipesapp.R
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun FavoritesScreen(
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Space16),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(Dimens.CornerExtraLarge),
                tonalElevation = Dimens.CardElevation,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            ) {
                Text(
                    text = "Здесь скоро появится список избранных рецептов",
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
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    RecipesAppTheme {
        FavoritesScreen()
    }
}