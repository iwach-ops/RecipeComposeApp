package com.wachtel.androidrecipesapp.app.di

import androidx.lifecycle.SavedStateHandle
import com.wachtel.androidrecipesapp.data.repository.RecipesRepository
import com.wachtel.androidrecipesapp.features.recipes.presentation.RecipesViewModel

class RecipesViewModelFactory(
    private val savedStateHandle: SavedStateHandle,
    private val repository: RecipesRepository
) : Factory<RecipesViewModel> {

    override fun create(): RecipesViewModel {
        return RecipesViewModel(
            savedStateHandle = savedStateHandle,
            repository = repository
        )
    }
}