package com.wachtel.androidrecipesapp.data.database.converter

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isBlank()) {
            emptyList()
        } else {
            value.split(SEPARATOR)
        }
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(SEPARATOR)
    }

    private companion object {
        const val SEPARATOR = "|||"
    }
}