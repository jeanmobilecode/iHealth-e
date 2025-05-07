package com.example.macroup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.recyclerView.AdapterRecipe
import com.example.macroup.recyclerView.Category
import com.example.macroup.recyclerView.CategoryAdapter
import androidx.activity.viewModels
import com.example.macroup.recyclerView.Ingredients
import com.example.macroup.recyclerView.Recipe
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val recipeListViewModel: MainActivityViewModel by viewModels() // Inicializando a ViewModel
    private lateinit var recipeAdapter: AdapterRecipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        splashScreen()
        setContentView(R.layout.activity_main)


        setupRecyclerView()

        recipeListViewModel.loadAllRecipes()

        observeRecipes()

        setBottomViewNavegation()

        generateRandomRecipe()



    }

    private fun generateRandomRecipe(){
        val randomRecipeButton: Button = findViewById(R.id.card_view_button)


        randomRecipeButton.setOnClickListener {
            val intent = Intent(this, RandomRecipesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun splashScreen(){
        // Inicialize a Splash Screen
        val splashScreen = installSplashScreen()

        // Adicionando um delay para garantir que a Splash Screen seja exibida por alguns segundos
        splashScreen.setKeepOnScreenCondition { false }
    }

    private fun setupRecyclerView(){

        //Category RecyclerView
        val categoryRecyclerView: RecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val categories = listOf(
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

        categoryRecyclerView.adapter = CategoryAdapter(categories) { category ->

            recipeListViewModel.loadRecipesByCategory(getString(category.categoryName))

        }

        //Home Recipes Recycler View
        val homeRecipesRecyclerView: RecyclerView = findViewById(R.id.homeRecipesRecyclerView)
        homeRecipesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recipeAdapter = AdapterRecipe(this, mutableListOf())
        homeRecipesRecyclerView.adapter = recipeAdapter

    }

    private fun observeRecipes() {
        // Observar mudanças na lista de receitas da ViewModel
        recipeListViewModel.recipeList.observe(this) { recipes ->
            val newRecipes = mutableListOf<Recipe>()
            for (recipe in recipes){
                newRecipes.add(recipe)
            }
            recipeAdapter.updateList(newRecipes)
        }
    }

    private fun setBottomViewNavegation(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

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


