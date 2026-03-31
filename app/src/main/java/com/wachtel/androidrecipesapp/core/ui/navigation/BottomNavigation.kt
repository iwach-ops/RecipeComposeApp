package com.wachtel.androidrecipesapp.core.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wachtel.androidrecipesapp.ui.theme.Dimens

@Composable
fun BottomNavigation(
    onCategoriesClick: () -> Unit,
    onRecipesClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = Dimens.Space16, vertical = Dimens.Space12),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space12)
    ) {
        Button(
            onClick = onCategoriesClick,
            modifier = Modifier
                .weight(1f)
                .height(Dimens.ButtonHeight),
            shape = RoundedCornerShape(Dimens.CornerLarge),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Text(
                text = "Категории",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = onRecipesClick,
            modifier = Modifier
                .weight(1f)
                .height(Dimens.ButtonHeight),
            shape = RoundedCornerShape(Dimens.CornerLarge),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Рецепты",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = onFavoriteClick,
            modifier = Modifier
                .weight(1f)
                .height(Dimens.ButtonHeight),
            shape = RoundedCornerShape(Dimens.CornerLarge),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text(
                text = "Избранное",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}