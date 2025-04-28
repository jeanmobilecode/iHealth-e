package com.example.macroup

import android.content.Intent
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
import com.google.android.material.bottomnavigation.BottomNavigationView

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
                searchRecipes(newText ?: "")
                return true
            }
        })

        setBottomViewNavegation()
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

    private fun setBottomViewNavegation(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_search

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> true

                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.nav_random -> {
                    startActivity(Intent(this, RandomRecipesActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.nav_fav -> {
                    startActivity(Intent(this, ShoppingActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                else -> false
            }
        }
    }


}
