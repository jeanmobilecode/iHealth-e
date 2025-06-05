package com.example.iHealthe.RecipeData

import android.util.Log
import com.example.iHealthe.Adapter.Ingredients
import com.example.iHealthe.Adapter.Instructions
import com.example.iHealthe.Adapter.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object RecipeRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getRecipesFiltered(query: String, category: String): List<Recipe> {
        return try {
            val collection = db.collection("recipes")
            val baseQuery = when {
                // ✅ Caso 1: Categoria = "Todas" e tem texto
                category == "Todas" && query.isNotBlank() -> {
                    collection
                        .orderBy("title")
                        .startAt(query)
                        .endAt(query + '\uf8ff')
                }

                // ✅ Caso 2: Categoria selecionada, sem texto
                category != "Todas" && query.isBlank() -> {
                    collection
                        .whereEqualTo("category", category)
                        .orderBy("title")
                }

                // ✅ Caso 3: Categoria selecionada, e texto digitado
                category != "Todas" && query.isNotBlank() -> {
                    collection
                        .whereEqualTo("category", category)
                        .orderBy("title")
                        .startAt(query)
                        .endAt(query + '\uf8ff')
                }

                // ✅ Caso 4: Nenhum filtro, nem texto
                else -> {
                    collection.orderBy("title")
                }
            }

            val result = baseQuery.get().await()

            result.documents.mapNotNull { it.toObject(Recipe::class.java) }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }





    suspend fun loadAllRecipes(): List<Recipe> {
        return try {
            val result = db.collection("recipes").limit(20).get().await()
            result.mapNotNull { it.toObject(Recipe::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun loadRecipesByCategory(category: String): List<Recipe> {
        return try {
            val result = db.collection("recipes")
                .whereEqualTo("category", category)
                .get().await()
            result.mapNotNull { it.toObject(Recipe::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun loadRandomRecipe(): Recipe? {
        val randomKey = (0..1000000).random()
        return try {
            val result = db.collection("recipes")
                .whereGreaterThanOrEqualTo("randomIndex", randomKey)
                .orderBy("randomIndex")
                .limit(1)
                .get().await()
            result.firstOrNull()?.toObject(Recipe::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loadIngredients(recipeId: String): List<Ingredients> {
        return try {
            val result = db.collection("recipes")
                .document(recipeId)
                .collection("ingredients")
                .get().await()
            result.mapNotNull { it.toObject(Ingredients::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun loadInstructions(recipeId: String): List<Instructions> {
        return try {
            val result = db.collection("recipes")
                .document(recipeId)
                .collection("instructions")
                .orderBy("step")
                .get().await()
            result.mapNotNull { it.toObject(Instructions::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addRecipes(recipeList: List<Recipe>) {
        val batch = db.batch()

        for (recipe in recipeList) {
            val recipeRef = db.collection("recipes").document(recipe.id)
            batch.set(recipeRef, recipe.copy(ingredients = emptyList(), instructions = emptyList()))

            for (ingredient in recipe.ingredients) {
                val ingredientRef = recipeRef.collection("ingredients").document()
                batch.set(ingredientRef, ingredient)
            }

            for (i in recipe.instructions.indices) {
                val instructionRef = recipeRef.collection("instructions").document()
                batch.set(instructionRef, Instructions(step = i + 1, text = recipe.instructions[i]))
            }
        }

        batch.commit()
            .addOnSuccessListener {
                Log.i("addRecipe", "Receitas adicionadas com sucesso!")
            }
            .addOnFailureListener {
                Log.e("addRecipe", "Erro ao adicionar as receitas", it)
            }
    }
}
