package com.logicalayer.ihealthe.utils

import android.app.Application
import com.logicalayer.ihealthe.RecipeData.RecipeViewModel

class MyApplication : Application() {
    val recipeViewModel: RecipeViewModel by lazy {
        RecipeViewModel(this)
    }
}