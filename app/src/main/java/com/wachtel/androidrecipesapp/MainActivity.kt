package com.wachtel.androidrecipesapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.wachtel.androidrecipesapp.core.network.NetworkConfig
import com.wachtel.androidrecipesapp.core.network.api.RecipesApiService
import com.wachtel.androidrecipesapp.data.model.CategoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@OptIn(ExperimentalSerializationApi::class)
class MainActivity : ComponentActivity() {

    private var deepLinkIntent by mutableStateOf<Intent?>(null)

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    private val recipesApiService: RecipesApiService by lazy {
        retrofit.create(RecipesApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "Метод onCreate() выполняется на потоке: ${Thread.currentThread().name}")

        enableEdgeToEdge()

        loadCategoriesFromNetwork()

        if (intent?.data != null) {
            deepLinkIntent = intent
        }

        setContent {
            RecipesApp(deepLinkIntent = deepLinkIntent)
        }
    }

    private fun loadCategoriesFromNetwork() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Запрос категорий на потоке: ${Thread.currentThread().name}")

                val categories = recipesApiService.getCategories()

                Log.d(
                    TAG,
                    "Поток: ${Thread.currentThread().name}. Получено категорий: ${categories.size}"
                )

                Log.d(
                    TAG,
                    "Поток: ${Thread.currentThread().name}. Категории: $categories"
                )

                categories.forEach { category ->
                    loadRecipesForCategory(category)
                }
            } catch (exception: Exception) {
                Log.e(TAG, "Ошибка при запросе категорий", exception)
            }
        }
    }

    private suspend fun loadRecipesForCategory(category: CategoryDto) {
        try {
            Log.d(
                TAG,
                "Запрос рецептов категории \"${category.title}\" на потоке: ${Thread.currentThread().name}"
            )

            val recipes = recipesApiService.getRecipesByCategory(category.id)

            Log.d(
                TAG,
                "Поток: ${Thread.currentThread().name}. Категория: ${category.title}. Рецептов: ${recipes.size}"
            )

            Log.d(
                TAG,
                "Поток: ${Thread.currentThread().name}. Рецепты категории \"${category.title}\": $recipes"
            )
        } catch (exception: Exception) {
            Log.e(
                TAG,
                "Ошибка при запросе рецептов. Категория: ${category.title}, id: ${category.id}",
                exception
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkIntent = if (intent.data != null) intent else null
    }

    companion object {
        private const val TAG = "Retrofit"
    }
}