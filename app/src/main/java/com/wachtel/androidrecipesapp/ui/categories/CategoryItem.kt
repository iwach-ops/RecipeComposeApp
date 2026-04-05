package com.wachtel.androidrecipesapp.ui.categories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.wachtel.androidrecipesapp.ui.categories.model.CategoryUiModel
import com.wachtel.androidrecipesapp.ui.theme.Dimens

@Composable
fun CategoryItem(
    category: CategoryUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fallbackPainter = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.CornerLarge),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimens.CardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        AsyncImage(
            model = category.imageUrl,
            contentDescription = category.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(
                    RoundedCornerShape(
                        topStart = Dimens.CornerLarge,
                        topEnd = Dimens.CornerLarge
                    )
                ),
            contentScale = ContentScale.Crop,
            placeholder = fallbackPainter,
            error = fallbackPainter
        )

        Column(
            modifier = Modifier.padding(Dimens.Space12)
        ) {
            Text(
                text = category.title.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = category.description,
                modifier = Modifier.padding(top = Dimens.Space8),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}