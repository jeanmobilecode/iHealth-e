package com.example.macroup

import  androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.macroup.recyclerView.Ingredients
import com.example.macroup.recyclerView.Instructions
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RecipeDetailsViewModel : ViewModel() {

    private val _recipeId = MutableLiveData<String>()
    val recipeId: LiveData<String> get() = _recipeId

    private val firestore = FirebaseFirestore.getInstance()

    fun setRecipeId(id: String) {
        _recipeId.value = id
    }

    fun getIngredientsOptions(): FirestoreRecyclerOptions<Ingredients>? {
        val id = _recipeId.value ?: return null

        val ingredientsRef = firestore.collection("recipes")
            .document(id)
            .collection("ingredients")

        return FirestoreRecyclerOptions.Builder<Ingredients>()
            .setQuery(ingredientsRef, Ingredients::class.java)
            .build()
    }

    fun getInstructionsOptions(): FirestoreRecyclerOptions<Instructions>? {
        val id = _recipeId.value ?: return null

        val instructionsRef = firestore.collection("recipes")
            .document(id)
            .collection("instructions")
            .orderBy("step", Query.Direction.ASCENDING)

        return FirestoreRecyclerOptions.Builder<Instructions>()
            .setQuery(instructionsRef, Instructions::class.java)
            .build()
    }
}
