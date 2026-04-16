package com.wachtel.androidrecipesapp.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme

@Composable
fun ScreenHeader(
    imagePainter: Painter,
    contentDescription: String,
    title: String,
    modifier: Modifier = Modifier,
    showShareButton: Boolean = false,
    onShareClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.HeaderHeight)
    ) {
        Image(
            painter = imagePainter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (showShareButton) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = Dimens.Space16,
                        end = Dimens.Space16
                    )
                    .clickable(onClick = onShareClick),
                shape = RoundedCornerShape(Dimens.CornerLarge),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                tonalElevation = Dimens.CardElevation
            ) {
                Text(
                    text = "Поделиться",
                    modifier = Modifier.padding(
                        horizontal = Dimens.Space16,
                        vertical = Dimens.Space12
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = Dimens.Space16,
                    bottom = Dimens.Space16
                ),
            shape = RoundedCornerShape(Dimens.CornerLarge),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            tonalElevation = Dimens.CardElevation
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(
                    horizontal = Dimens.Space16,
                    vertical = Dimens.Space12
                ),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenHeaderPreview() {
    RecipesAppTheme {
        ScreenHeader(
            imagePainter = ColorPainter(Color(0xFFD8C4F8)),
            contentDescription = "Preview header",
            title = "Категории",
            showShareButton = true
        )
    }
}