package com.example.macroup.RecipeData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.macroup.recyclerView.Ingredients
import com.example.macroup.recyclerView.Instructions
import com.example.macroup.recyclerView.Recipe
import com.firebase.ui.firestore.FirestoreRecyclerOptions

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

    private val _ingredientsOptions = MutableLiveData<FirestoreRecyclerOptions<Ingredients>>()
    val ingredientsOptions: LiveData<FirestoreRecyclerOptions<Ingredients>> get() = _ingredientsOptions

    private val _instructionsOptions = MutableLiveData<FirestoreRecyclerOptions<Instructions>>()
    val instructionsOptions: LiveData<FirestoreRecyclerOptions<Instructions>> get() = _instructionsOptions

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
            onFailure = {
                // Pode adicionar um MutableLiveData de erro aqui, se necessário
            }
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
        loadIngredientsOptions(id)
        loadInstructionsOptions(id)
    }

    private fun loadIngredientsOptions(id: String) {
        val query = RecipeRepository.getIngredientsOptions(id)
        val options = FirestoreRecyclerOptions.Builder<Ingredients>()
            .setQuery(query, Ingredients::class.java)
            .build()
        _ingredientsOptions.value = options
    }

    private fun loadInstructionsOptions(id: String) {
        val query = RecipeRepository.getInstructionsOptions(id)
        val options = FirestoreRecyclerOptions.Builder<Instructions>()
            .setQuery(query, Instructions::class.java)
            .build()
        _instructionsOptions.value = options
    }

    fun updateRecipeList(recipes: List<Recipe>) {
        _recipeList.value = recipes
    }
}
