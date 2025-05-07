package com.example.macroup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView


class RandomRecipesActivity : AppCompatActivity() {

    private lateinit var img : ImageView
    private lateinit var title : TextView
    private lateinit var category: TextView
    private lateinit var time: TextView
    private lateinit var calories: TextView
    private lateinit var protein: TextView
    private lateinit var carbs: TextView
    private lateinit var fat: TextView
    private lateinit var newRecipe: Button
    private lateinit var cardView : CardView
    private val viewModel: RandomRecipesViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_random_recipes)

        setID()

        setBottomViewNavegation()

        observer()

        viewModel.loadRandomRecipe()

        newRecipe.setOnClickListener {
            viewModel.loadRandomRecipe()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setID(){
        img = findViewById(R.id.recipeImage)
        title = findViewById(R.id.recipeTitle)
        category = findViewById(R.id.recipeCategory)
        time = findViewById(R.id.recipeTime)
        protein = findViewById(R.id.descriptionProtein)
        carbs = findViewById(R.id.descriptionCarbs)
        fat = findViewById(R.id.descriptionFat)
        calories = findViewById(R.id.recipeCalories)
        newRecipe = findViewById(R.id.generateButton)
        cardView = findViewById(R.id.card_view)
    }


    private fun observer() {
        viewModel.recipeList.observe(this) { recipes ->
            if (recipes.isNotEmpty()) {
                val recipe = recipes[0]

                // Atualiza os campos da UI com os dados da receita
                title.text = recipe.title
                category.text = recipe.category
                time.text = "${recipe.time} min"
                calories.text = "${recipe.kcal} kcal"
                protein.text = recipe.protein
                carbs.text = recipe.carbohydrates
                fat.text = recipe.fat

                val imageResId = img.context.resources.getIdentifier(
                    recipe.image, "drawable", img.context.packageName
                )
                img.setImageResource(imageResId)

                // Clique no CardView leva para os detalhes
                cardView.setOnClickListener {
                    val intent = Intent(this, RecipeDetailsActivity::class.java)
                    intent.putExtra("RECIPE", recipe)
                    startActivity(intent)
                }
            }
        }
    }


    private fun setBottomViewNavegation(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_random

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_random -> true

                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
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