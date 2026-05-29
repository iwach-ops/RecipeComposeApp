package com.wachtel.androidrecipesapp.app.di

interface Factory<T> {

    fun create(): T
}