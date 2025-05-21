package com.example.macroup.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.macroup.R
import com.example.macroup.RecipeData.RecipeViewModel
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
    private lateinit var shareButton: Button
    private val recipeViewModel: RecipeViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_random_recipes)

        setID()

        setBottomViewNavegation()

        observer()

        recipeViewModel.loadRandomRecipe()

        newRecipe.setOnClickListener {
            recipeViewModel.loadRandomRecipe()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setID(){
        shareButton = findViewById(R.id.shareButton)
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
        recipeViewModel.randomRecipe.observe(this) { recipe ->
            recipe?.let {
                // Atualiza os campos da UI com os dados da receita
                title.text = it.title
                category.text = it.category
                time.text = it.time
                calories.text = "${it.kcal} kcal"
                protein.text = it.protein
                carbs.text = it.carbohydrates
                fat.text = it.fat

                val imageResId = img.context.resources.getIdentifier(
                    it.image, "drawable", img.context.packageName
                )
                img.setImageResource(imageResId)

                val appLink = "https://play.google.com/store/apps/details?id=${packageName}"

                // Clique para compartilhar
                shareButton.setOnClickListener {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Confira esta receita no MacroUp!")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            """
                    ${recipe.title}
                    
                    Categoria: ${recipe.category}
                    Tempo de preparo: ${recipe.time} min
                    
                    Macros por porção:
                    
                    - Proteínas: ${recipe.protein}
                    - Carboidratos: ${recipe.carbohydrates}
                    - Gorduras: ${recipe.fat}

                    Veja os ingredientes e instruções no MacroUp:
                    
                    $appLink
                    """.trimIndent()
                        )
                    }
                    startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
                }

                // Clique no CardView leva para os detalhes
                cardView.setOnClickListener {
                    recipeViewModel.setCurrentRecipeId(recipe.id)
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
                R.id.nav_random -> {
                    true
                }

                R.id.nav_fav -> {
                    val intent = Intent(this, ShoppingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.nav_search -> {
                    val intent = Intent(this, SearchActivity::class.java)
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