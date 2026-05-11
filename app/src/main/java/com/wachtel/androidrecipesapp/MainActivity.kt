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
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private var deepLinkIntent by mutableStateOf<Intent?>(null)

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
        threadPool.execute {
            var connection: HttpURLConnection? = null

            try {
                Log.d(TAG, "Запрос категорий на потоке: ${Thread.currentThread().name}")

                connection = URL(CATEGORIES_URL).openConnection() as HttpURLConnection

                val responseBody = executeGetRequest(connection)
                val categories = json.decodeFromString<List<CategoryDto>>(responseBody)

                Log.d(
                    TAG,
                    "Поток: ${Thread.currentThread().name}. Получено категорий: ${categories.size}"
                )

                categories.forEach { category ->
                    loadRecipesForCategory(category)
                }
            } catch (exception: Exception) {
                Log.e(TAG, "Ошибка при запросе категорий", exception)
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun loadRecipesForCategory(category: CategoryDto) {
        threadPool.execute {
            var connection: HttpURLConnection? = null

            try {
                Log.d(
                    TAG,
                    "Запрос рецептов категории \"${category.title}\" на потоке: ${Thread.currentThread().name}"
                )

                val recipesUrl = "$CATEGORIES_URL/${category.id}/recipes"
                connection = URL(recipesUrl).openConnection() as HttpURLConnection

                val responseBody = executeGetRequest(connection)

                val recipesCount = json
                    .parseToJsonElement(responseBody)
                    .jsonArray
                    .size

                Log.d(
                    TAG,
                    "Поток: ${Thread.currentThread().name}. Категория: ${category.title}. Рецептов: $recipesCount"
                )
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Ошибка при запросе рецептов. Категория: ${category.title}, id: ${category.id}",
                    exception
                )
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun executeGetRequest(connection: HttpURLConnection): String {
        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000

        val responseCode = connection.responseCode

        val responseBody = if (responseCode in 200..299) {
            connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
        }

        if (responseCode !in 200..299) {
            throw IllegalStateException("Код ответа: $responseCode. Тело ответа: $responseBody")
        }

        return responseBody
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