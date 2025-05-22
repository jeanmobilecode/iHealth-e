package com.example.macroup.RecipeData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.macroup.recyclerView.Ingredients
import com.example.macroup.recyclerView.Instructions
import com.example.macroup.recyclerView.Recipe

class RecipeViewModel : ViewModel() {

    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: LiveData<List<Recipe>> = _recipeList

    private val _isRecipesLoaded = MutableLiveData<Boolean>()
    val isRecipesLoaded: LiveData<Boolean> = _isRecipesLoaded

    private val _recipeDetails = MutableLiveData<Recipe>()
    val recipeDetails: LiveData<Recipe> get() = _recipeDetails

    private val _randomRecipe = MutableLiveData<Recipe?>()
    val randomRecipe: LiveData<Recipe?> = _randomRecipe

    private val _currentRecipeId = MutableLiveData<String?>()
    val currentRecipeId: LiveData<String?> = _currentRecipeId

    private val _ingredientsList = MutableLiveData<List<Ingredients>>()
    val ingredientsList: LiveData<List<Ingredients>> get() = _ingredientsList

    private val _instructionsList = MutableLiveData<List<Instructions>>()
    val instructionsList: LiveData<List<Instructions>> get() = _instructionsList

    fun loadAllRecipes() {
        RecipeRepository.loadAllRecipes(
            onSuccess = { recipes ->
                _recipeList.value = recipes
                _isRecipesLoaded.value = true
            },
            onFailure = {
                _isRecipesLoaded.value = false
            }
        )
    }

    fun loadRecipesByCategory(category: String) {
        RecipeRepository.loadRecipesByCategory(
            category,
            onSuccess = { recipes ->
                _recipeList.value = recipes
            },
            onFailure = { }
        )
    }

    fun loadRandomRecipe() {
        RecipeRepository.loadRandomRecipe(
            onSuccess = { recipe ->
                _randomRecipe.value = recipe
            },
            onFailure = {
                _randomRecipe.value = null
            }
        )
    }

    fun setCurrentRecipeId(id: String) {
        _currentRecipeId.value = id
        loadIngredients(id)
        loadInstructions(id)
    }

    fun loadIngredients(recipeId: String) {
        RecipeRepository.loadIngredients(
            recipeId,
            onSuccess = { ingredients ->
                _ingredientsList.value = ingredients
            },
            onFailure = {
                _ingredientsList.value = emptyList()
            }
        )
    }

    fun loadInstructions(recipeId: String) {
        RecipeRepository.loadInstructions(
            recipeId,
            onSuccess = { instructions ->
                _instructionsList.value = instructions
            },
            onFailure = {
                _instructionsList.value = emptyList()
            }
        )
    }

    fun updateRecipeList(recipes: List<Recipe>) {
        _recipeList.value = recipes
    }
}
