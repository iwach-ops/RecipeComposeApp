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
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {

    private var deepLinkIntent by mutableStateOf<Intent?>(null)

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
        Thread {
            Log.d(TAG, "Выполняю запрос на потоке: ${Thread.currentThread().name}")

            val connection = URL(CATEGORIES_URL).openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000

                val responseCode = connection.responseCode

                val responseBody = if (responseCode in 200..299) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                }

                Log.d(TAG, "Тело ответа: $responseBody")

                if (responseCode in 200..299) {
                    val categories = Json.decodeFromString<List<CategoryDto>>(responseBody)

                    Log.d(TAG, "Количество полученных категорий: ${categories.size}")

                    categories.forEach { category ->
                        Log.d(TAG, "Категория: ${category.title}")
                    }
                } else {
                    Log.e(TAG, "Ошибка запроса. Код ответа: $responseCode")
                }
            } catch (exception: Exception) {
                Log.e(TAG, "Ошибка при выполнении сетевого запроса", exception)
            } finally {
                connection.disconnect()
            }
        }.start()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkIntent = if (intent.data != null) intent else null
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val CATEGORIES_URL = "https://recipes.androidsprint.ru/api/category"
    }
}