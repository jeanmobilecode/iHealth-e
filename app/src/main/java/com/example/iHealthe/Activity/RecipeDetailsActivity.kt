package com.example.iHealthe.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iHealthe.R
import com.example.iHealthe.RecipeData.RecipeViewModel
import com.example.iHealthe.Adapter.AdapterIngredients
import com.example.iHealthe.Adapter.AdapterInstructions
import com.example.iHealthe.Adapter.Recipe
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class RecipeDetailsActivity : AppCompatActivity() {

    private val recipeViewModel: RecipeViewModel by viewModels()

    private lateinit var ingredientsAdapter: AdapterIngredients
    private lateinit var ingredientsRecyclerView: RecyclerView

    private lateinit var instructionAdapter: AdapterInstructions
    private lateinit var instructionRecyclerView: RecyclerView

    private lateinit var shareButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_recipe_details)

        MobileAds.initialize(this) {}

        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val recipe = intent.getParcelableExtra<Recipe>("RECIPE")!!

        recipeViewModel.setCurrentRecipeId(recipe.id)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setRecipeInfo(recipe)
        ingredientsSetup()
        instructionsSetup()
        shareRecipe(recipe)

    }

    private fun setRecipeInfo(recipe: Recipe){
        val title: TextView = findViewById(R.id.recipeTitle)
        val recipeImage: ImageView = findViewById(R.id.recipeImage)
        val kcal: TextView = findViewById(R.id.kcalText)
        val protein: TextView = findViewById(R.id.proteinText)
        val carbs: TextView = findViewById(R.id.carbsText)
        val fat: TextView = findViewById(R.id.fatText)
        val time: TextView = findViewById(R.id.clockText)

        title.text = recipe.title

        val imageResId = recipeImage.context.resources.getIdentifier(recipe.image, "drawable", recipeImage.context.packageName)
        recipeImage.setImageResource(imageResId)

        kcal.text = "${recipe.kcal} kcal"
        protein.text = "${recipe.protein}g"
        carbs.text = "${recipe.carbohydrates}g"
        fat.text = "${recipe.fat}g"
        time.text = "${recipe.time}min"
    }

    private fun instructionsSetup(){
        // Instruções RecyclerView
        instructionRecyclerView = findViewById(R.id.instructionsRecyclerView)
        instructionRecyclerView.layoutManager = LinearLayoutManager(this)
        instructionAdapter = AdapterInstructions(emptyList())
        instructionRecyclerView.adapter = instructionAdapter

        recipeViewModel.instructionsList.observe(this) { instructions ->
            instructionAdapter.updateList(instructions)
        }
    }

    private fun ingredientsSetup(){
        // Ingredientes RecyclerView
        ingredientsRecyclerView = findViewById(R.id.ingredients)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        ingredientsAdapter = AdapterIngredients(this, emptyList())
        ingredientsRecyclerView.adapter = ingredientsAdapter

        recipeViewModel.ingredientsList.observe(this) { ingredients ->
            ingredientsAdapter.updateList(ingredients)
        }
    }

    private fun shareRecipe(recipe: Recipe){
        shareButton = findViewById(R.id.shareButton)
        val appLink = "https://play.google.com/store/apps/details?id=${packageName}"

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
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
