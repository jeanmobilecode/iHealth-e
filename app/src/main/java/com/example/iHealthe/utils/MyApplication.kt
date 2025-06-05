package com.example.iHealthe.utils

import android.app.Application
import com.example.iHealthe.RecipeData.RecipeViewModel

class MyApplication : Application() {
    val recipeViewModel: RecipeViewModel by lazy {
        RecipeViewModel(this)
    }
}