package com.example.macroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.macroup.recyclerView.Recipe
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore


class RandomRecipesActivity : AppCompatActivity() {

    val img : ImageView = findViewById(R.id.recipeImage)
    val title : TextView = findViewById(R.id.recipeTitle)
    val category: TextView = findViewById(R.id.recipeCategory)
    val time: TextView = findViewById(R.id.recipeTime)
    val calories: TextView = findViewById(R.id.recipeCalories)
    private val db = FirebaseFirestore.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_random_recipes)

        setBottomViewNavegation()

    }

    private fun loadNewRecipe(){
            db.collection("recipes")
                .get()
                .addOnSuccessListener { result ->
                    val recipes = mutableListOf<Recipe>()
                    for (document in result) {
                        val recipe = document.toObject(Recipe::class.java)
                        recipes.add(recipe)
                    }
                    _recipeList.setValue(recipes)
                    Log.i("log_recipeList","posted: ${_recipeList.value}")
                }

                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Erro ao carregar receitas: ${exception.message}")
                }
    }

    private fun setBottomViewNavegation(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_random

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_random -> true

                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
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



/*



*/