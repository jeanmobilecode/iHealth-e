package com.example.macroup.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.recyclerView.AdapterRecipe
import androidx.activity.viewModels
import com.example.macroup.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.macroup.RecipeData.RecipeViewModel
import com.example.macroup.recyclerView.Category
import com.example.macroup.recyclerView.CategoryAdapter
import com.example.macroup.recyclerView.Recipe
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class MainActivity : AppCompatActivity() {

    private val recipeViewModel: RecipeViewModel by viewModels()
    private lateinit var recipeAdapter: AdapterRecipe
    private var loadedRecipes: ArrayList<Recipe>? = null
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        MobileAds.initialize(this){}

        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        setupRecyclerView()
        setBottomViewNavigation()
        generateRandomRecipe()
        observeRecipes()
        loadToMainActivity()

    }

    private fun loadToMainActivity(){
        if (recipeViewModel.recipeList.value.isNullOrEmpty()) {
            loadedRecipes = intent.getParcelableArrayListExtra("RECIPES")

            if (!loadedRecipes.isNullOrEmpty()) {
                recipeViewModel.updateRecipeList(loadedRecipes!!)
            } else {
                recipeViewModel.loadAllRecipes()
            }
        } else {
            // Atualiza a lista com os dados existentes no ViewModel
            updateRecipeList(ArrayList(recipeViewModel.recipeList.value!!))
        }
    }

    private fun updateRecipeList(recipes: ArrayList<Recipe>) {
        recipeAdapter.updateRecipes(recipes)
    }

    private fun generateRandomRecipe() {
        val randomRecipeButton: Button = findViewById(R.id.card_view_button)
        randomRecipeButton.setOnClickListener {
            val intent = Intent(this, RandomRecipesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val categoryRecyclerView: RecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val categories = listOf(
            Category(R.string.all_categories, R.drawable.img_all_categories),
            Category(R.string.breakfast, R.drawable.img_breakfast),
            Category(R.string.lunch_and_dinner, R.drawable.img_lunch_and_dinner),
            Category(R.string.high_protein, R.drawable.img_high_protein),
            Category(R.string.post_workout, R.drawable.img_post_workout),
            Category(R.string.pre_workout, R.drawable.img_pre_workout),
            Category(R.string.vegan, R.drawable.img_vegan),
            Category(R.string.vegetarian, R.drawable.img_vegetarian),
            Category(R.string.snack, R.drawable.img_snack),
            Category(R.string.lowcarb, R.drawable.img_lowcarb),
            Category(R.string.fit_dessert, R.drawable.img_fit_dessert)
        )

        val categoryAdapter = CategoryAdapter(categories) { category ->
            val categoryName = getString(category.categoryName)

            if (categoryName == getString(R.string.all_categories)) {
                recipeViewModel.loadAllRecipes()
            } else {
                recipeViewModel.loadRecipesByCategory(categoryName)
            }
        }

        categoryRecyclerView.adapter = categoryAdapter

        val homeRecipesRecyclerView: RecyclerView = findViewById(R.id.homeRecipesRecyclerView)
        homeRecipesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recipeAdapter = AdapterRecipe(this, mutableListOf())
        homeRecipesRecyclerView.adapter = recipeAdapter
    }

    private fun observeRecipes() {
        recipeViewModel.recipeList.observe(this) { recipes ->
            if (recipes.isNotEmpty()) {
                loadedRecipes = ArrayList(recipes)
                updateRecipeList(loadedRecipes!!)
            } else {
                Toast.makeText(this, "Nenhuma receita disponível", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    private fun setBottomViewNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true  // Já está aqui

                R.id.nav_search -> {
                    val intent = Intent(this, SearchActivity::class.java)
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

                R.id.nav_fav -> {
                    val intent = Intent(this, ShoppingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }


}















