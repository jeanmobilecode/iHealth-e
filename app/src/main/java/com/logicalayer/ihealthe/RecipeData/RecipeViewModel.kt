package com.logicalayer.ihealthe.RecipeData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.logicalayer.ihealthe.Adapter.Ingredients
import com.logicalayer.ihealthe.Adapter.Instructions
import com.logicalayer.ihealthe.Adapter.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

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

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            val filteredRecipes = withContext(Dispatchers.IO) {
                RecipeRepository.getRecipesFiltered(query)
            }
            _recipeList.value = filteredRecipes
        }
    }

    fun loadAllRecipes() {
        viewModelScope.launch {
            try {
                val recipes = withContext(Dispatchers.IO) {
                    RecipeRepository.loadAllRecipes()
                }
                _recipeList.value = recipes
                _isRecipesLoaded.value = true
            } catch (e: Exception) {
                _isRecipesLoaded.value = false
            }
        }
    }

    fun loadRecipesByCategory(category: String) {
        viewModelScope.launch {
            try {
                val recipes = withContext(Dispatchers.IO) {
                    RecipeRepository.loadRecipesByCategory(category)
                }
                _recipeList.value = recipes
            } catch (_: Exception) {
            }
        }
    }

    fun loadRandomRecipe() {
        viewModelScope.launch {
            try {
                val recipe = withContext(Dispatchers.IO) {
                    RecipeRepository.loadRandomRecipe()
                }
                _randomRecipe.value = recipe
            } catch (e: Exception) {
                _randomRecipe.value = null
            }
        }
    }

    fun setCurrentRecipeId(id: String) {
        _currentRecipeId.value = id
        loadIngredients(id)
        loadInstructions(id)
    }

    fun loadIngredients(recipeId: String) {
        viewModelScope.launch {
            try {
                val ingredients = withContext(Dispatchers.IO) {
                    RecipeRepository.loadIngredients(recipeId)
                }
                _ingredientsList.value = ingredients
            } catch (e: Exception) {
                _ingredientsList.value = emptyList()
            }
        }
    }

    fun loadInstructions(recipeId: String) {
        viewModelScope.launch {
            try {
                val instructions = withContext(Dispatchers.IO) {
                    RecipeRepository.loadInstructions(recipeId)
                }
                _instructionsList.value = instructions
            } catch (e: Exception) {
                _instructionsList.value = emptyList()
            }
        }
    }
}
