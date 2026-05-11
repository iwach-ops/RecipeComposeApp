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
import com.wachtel.androidrecipesapp.data.model.CategoryDto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {

    private var deepLinkIntent by mutableStateOf<Intent?>(null)

    private val okHttpClient = OkHttpClient()

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(10)

    private val json = Json {
        ignoreUnknownKeys = true
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
        thread(name = "CategoriesThread") {
            try {
                Log.d(TAG, "Запрос категорий на потоке: ${Thread.currentThread().name}")

                val responseBody = executeGetRequest(CATEGORIES_URL)
                val categories = json.decodeFromString<List<CategoryDto>>(responseBody)

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

    private fun loadRecipesForCategory(category: CategoryDto) {
        threadPool.execute {
            try {
                Log.d(
                    TAG,
                    "Запрос рецептов категории \"${category.title}\" на потоке: ${Thread.currentThread().name}"
                )

                val recipesUrl = "$CATEGORIES_URL/${category.id}/recipes"
                val responseBody = executeGetRequest(recipesUrl)

                val recipesCount = json
                    .parseToJsonElement(responseBody)
                    .jsonArray
                    .size

                Log.d(
                    TAG,
                    "Поток: ${Thread.currentThread().name}. Категория: ${category.title}. Рецептов: $recipesCount"
                )

                Log.d(
                    TAG,
                    "Поток: ${Thread.currentThread().name}. Ответ рецептов категории \"${category.title}\": $responseBody"
                )
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Ошибка при запросе рецептов. Категория: ${category.title}, id: ${category.id}",
                    exception
                )
            }
        }
    }

    private fun executeGetRequest(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                throw IllegalStateException(
                    "Код ответа: ${response.code}. Тело ответа: $responseBody"
                )
            }

            return responseBody
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkIntent = if (intent.data != null) intent else null
    }

    override fun onDestroy() {
        threadPool.shutdown()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "Pool"
        private const val CATEGORIES_URL = "https://recipes.androidsprint.ru/api/category"
    }
}