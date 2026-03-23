package com.wachtel.androidrecipesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.wachtel.androidrecipesapp.ui.theme.Dimens
import com.wachtel.androidrecipesapp.ui.theme.RecipesAppTheme
import androidx.compose.foundation.shape.RoundedCornerShape

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipesAppTheme {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.Space16)
                        ) {
                            Text(
                                text = "Recipes App",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.displayLarge
                            )

                            Surface(
                                modifier = Modifier.padding(top = Dimens.Space16),
                                color = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                shape = RoundedCornerShape(Dimens.CornerLarge),
                                shadowElevation = Dimens.CardElevation
                            ) {
                                Column(
                                    modifier = Modifier.padding(Dimens.Space16)
                                ) {
                                    Text(
                                        text = "Добро пожаловать в приложение рецептов",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = "Категории",
                                        color = MaterialTheme.colorScheme.tertiary,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(top = Dimens.Space8)
                                    )

                                    Text(
                                        text = "Избранное",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(top = Dimens.Space8)
                                    )

                                    Text(
                                        text = "Здесь позже будут карточки рецептов, категории и фильтры.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = Dimens.Space8)
                                    )
                                    Surface(
                                        modifier = Modifier.padding(top = Dimens.Space12),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        shape = RoundedCornerShape(Dimens.CornerMedium)
                                    ) {
                                        Text(
                                            text = "Неактивный фильтр",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(
                                                horizontal = Dimens.Space12,
                                                vertical = Dimens.Space8
                                            )
                                        )
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(top = Dimens.Space16),
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}