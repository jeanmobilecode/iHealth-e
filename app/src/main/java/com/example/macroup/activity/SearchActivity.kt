package com.example.macroup.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
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
import com.example.macroup.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SearchActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: AdapterRecipe
    private val recipeList = mutableListOf<Recipe>() // modelo que você já usa
    private var selectedCategory: String = "Todas"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_search)

        searchRecipes("")

        //Codigo Adapter Receitas Resultado Pesquisa
        recyclerView = findViewById(R.id.recycler_view)
        recipeAdapter = AdapterRecipe(this, recipeList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter

        //Chip Group
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            selectedCategory = chip?.text?.toString() ?: "Todas"
            searchRecipes(searchView.query.toString())
        }


        //Codigo searchView
        searchView = findViewById(R.id.search_view)
        searchView.isIconified = false
        searchView.queryHint = getString(R.string.search_hint)

        val plate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        plate?.setBackgroundColor(Color.TRANSPARENT)

        val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)

        closeButton.setOnClickListener {
            // Evita o comportamento padrão
            searchView.setQuery("", false)
            searchView.clearFocus() // Remove o foco para não abrir o teclado
        }

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

    override fun onResume() {
        super.onResume()
        searchView.clearFocus() // Remove o foco
        searchView.isIconified = true // Garante que a SearchView começa "iconificada"
    }

    private fun searchRecipes(query: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                recipeList.clear()

                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)

                    val matchesQuery = recipe.title.contains(query, ignoreCase = true)
                    val matchesCategory = selectedCategory == "Todas" || recipe.category.equals(selectedCategory, ignoreCase = true)

                    if ((query.isBlank() || matchesQuery) && matchesCategory) {
                        recipeList.add(recipe)
                    }
                }

                recipeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar receitas", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setBottomViewNavegation(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_search

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> {
                    true
                }

                R.id.nav_fav -> {
                    val intent = Intent(this, ShoppingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.nav_random -> {
                    val intent = Intent(this, RandomRecipesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}
