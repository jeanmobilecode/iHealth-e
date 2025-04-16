package com.example.macroup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.macroup.recyclerView.Recipe
import com.google.firebase.firestore.FirebaseFirestore

class RandomRecipesViewModel : ViewModel() {

    private val _recipeList = MutableLiveData<List<Recipe>>()

    val recipeList: LiveData<List<Recipe>> = _recipeList

    fun loadRandomRecipe() {
        val db = FirebaseFirestore.getInstance()
        val randomKey = (0..1000000).random()

        db.collection("recipes")
            .whereGreaterThanOrEqualTo("randomIndex", randomKey)
            .orderBy("randomIndex")
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val recipe = result.first().toObject(Recipe::class.java) // converte para objeto Recipe
                    _recipeList.value = listOf(recipe)
                    Log.i("log_randomRecipe", "Receita aleatória encontrada: ${recipe.title}")
                } else {
                    db.collection("recipes")
                        .orderBy("randomIndex")
                        .limit(1)
                        .get()
                        .addOnSuccessListener { fallbackResult ->
                            val fallbackRecipe = fallbackResult.firstOrNull()?.toObject(Recipe::class.java)
                            _recipeList.value = listOfNotNull(fallbackRecipe) // seta a receita fallback, se houver
                            Log.i("log_randomRecipe", "Receita fallback: ${fallbackRecipe?.title}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar receita aleatória: ${e.message}")
            }
    }

}


