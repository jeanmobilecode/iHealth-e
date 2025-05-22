package com.example.macroup.RecipeData

import com.example.macroup.recyclerView.Ingredients
import com.example.macroup.recyclerView.Instructions
import com.example.macroup.recyclerView.Recipe
import com.google.firebase.firestore.FirebaseFirestore

object RecipeRepository {

    private val db = FirebaseFirestore.getInstance()

    fun loadAllRecipes(onSuccess: (List<Recipe>) -> Unit, onFailure: () -> Unit) {
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                val recipes = result.mapNotNull { it.toObject(Recipe::class.java) }
                onSuccess(recipes)
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun loadRecipesByCategory(category: String, onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("recipes")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { result ->
                val recipes = result.mapNotNull { it.toObject(Recipe::class.java) }
                onSuccess(recipes)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun loadRandomRecipe(onSuccess: (Recipe?) -> Unit, onFailure: (Exception) -> Unit) {
        val randomKey = (0..1000000).random()

        db.collection("recipes")
            .whereGreaterThanOrEqualTo("randomIndex", randomKey)
            .orderBy("randomIndex")
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val recipe = result.firstOrNull()?.toObject(Recipe::class.java)
                onSuccess(recipe)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun loadIngredients(recipeId: String, onSuccess: (List<Ingredients>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("recipes")
            .document(recipeId)
            .collection("ingredients")
            .get()
            .addOnSuccessListener { result ->
                val ingredients = result.mapNotNull { it.toObject(Ingredients::class.java) }
                onSuccess(ingredients)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun loadInstructions(recipeId: String, onSuccess: (List<Instructions>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("recipes")
            .document(recipeId)
            .collection("instructions")
            .get()
            .addOnSuccessListener { result ->
                val instructions = result.mapNotNull { it.toObject(Instructions::class.java) }
                onSuccess(instructions)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}
