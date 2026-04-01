package com.wachtel.androidrecipesapp.ui.recipes

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.wachtel.androidrecipesapp.core.ui.ScreenHeader
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun RecipesScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenHeader(
            imagePainter = ColorPainter(Color(0xFFDCE8FF)),
            contentDescription = "Рецепты",
            title = "Рецепты"
        )

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
                    text = "Скоро здесь будет список рецептов",
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
private fun RecipesScreenPreview() {
    RecipesAppTheme {
        RecipesScreen()
    }
}