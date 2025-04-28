package com.example.macroup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.macroup.recyclerView.Instructions
import com.example.macroup.recyclerView.Recipe
import com.google.firebase.firestore.FirebaseFirestore

class MainActivityViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: LiveData<List<Recipe>> = _recipeList



    fun addRecipes(recipeList: List<Recipe>) {
        //Criamos um batch para agrupar as operações e evitar várias chamadas no fireStore
        val batch = db.batch()

        //Como queremos adicionar varias receitas, criamos uma estrutura de repetição para add todas as receitas da lista
        for (recipe in recipeList) {
            val recipeRef = db.collection("recipes").document(recipe.id) // Usa o ID da receita para criar uma referência do documento
            batch.set(recipeRef, recipe.copy(ingredients = emptyList(), instructions = emptyList())) // Salva a receita sem os ingredientes e instruções (que serão subcoleções)

            // Adiciona os ingredientes como subcoleção
            for (ingredient in recipe.ingredients) {
                val ingredientRef = recipeRef.collection("ingredients").document()
                batch.set(ingredientRef, ingredient)
            }

            // Adiciona as instruções como subcoleção
            for (i in recipe.instructions.indices) {
                val instructionRef = recipeRef.collection("instructions").document()
                batch.set(instructionRef, Instructions(step = i + 1, text = recipe.instructions[i]))
            }
        }

        // Envia todas as operações de uma vez
        batch.commit()
            .addOnSuccessListener {
                Log.i("addRecipe", "Receitas adicionadas com sucesso!")
            }
            .addOnFailureListener {
                Log.e("addRecipe", "Erro ao adicionar as receitas", it)
            }
    }


    fun loadRecipesByCategory(category: String) {
        db.collection("recipes")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { result ->
                val recipes = mutableListOf<Recipe>()
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipes.add(recipe)
                }
                _recipeList.value = recipes
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao carregar receitas: ${exception.message}")
            }
    }

    fun loadAllRecipes() {
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                val recipes = mutableListOf<Recipe>()
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipes.add(recipe)
                }
                _recipeList.setValue(recipes)
            }

            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao carregar receitas: ${exception.message}")
            }
    }


}