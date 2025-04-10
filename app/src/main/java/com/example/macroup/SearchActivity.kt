package com.example.macroup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.recyclerView.AdapterRecipe
import com.example.macroup.recyclerView.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat

class SearchActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: AdapterRecipe
    private val recipeList = mutableListOf<Recipe>() // modelo que você já usa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_search)

        searchRecipes("")

        searchView = findViewById(R.id.search_view)
        recyclerView = findViewById(R.id.recycler_view)

        searchView.isIconified = false

        recipeAdapter = AdapterRecipe(this, recipeList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchRecipes(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRecipes(newText ?: "") // atualiza conforme o usuário digita
                return true
            }
        })
    }

    private fun searchRecipes(query: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                recipeList.clear()

                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)

                    val matchesQuery = recipe.title.contains(query, ignoreCase = true) ||
                            recipe.category.contains(query, ignoreCase = true)

                    if (query.isBlank() || matchesQuery) {
                        recipeList.add(recipe)
                    }
                }

                recipeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar receitas", Toast.LENGTH_SHORT).show()
            }
    }


}
