package com.example.macroup.RecipeData

import com.example.macroup.recyclerView.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object RecipeRepository {

    private val db = FirebaseFirestore.getInstance()

    fun loadAllRecipes(onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                val recipes = result.mapNotNull { it.toObject(Recipe::class.java) }
                onSuccess(recipes)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
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

    fun getIngredientsOptions(recipeId: String): Query {
        return db.collection("recipes")
            .document(recipeId)
            .collection("ingredients")
    }

    fun getInstructionsOptions(recipeId: String): Query {
        return db.collection("recipes")
            .document(recipeId)
            .collection("instructions")
            .orderBy("step", Query.Direction.ASCENDING)
    }
}
