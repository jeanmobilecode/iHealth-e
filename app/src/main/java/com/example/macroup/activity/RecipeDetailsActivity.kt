package com.example.macroup.activity

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
import com.example.macroup.R
import com.example.macroup.RecipeData.RecipeViewModel
import com.example.macroup.recyclerView.AdapterIngredients
import com.example.macroup.recyclerView.AdapterInstructions
import com.example.macroup.recyclerView.Recipe
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

        MobileAds.initialize(this){}

        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Recebe os dados da receita
        val recipe = intent.getParcelableExtra<Recipe>("RECIPE")!!

        // Passa o ID da receita para o ViewModel
        recipeViewModel.setCurrentRecipeId(recipe.id)

        // Referências do layout
        val title: TextView = findViewById(R.id.recipeTitle)
        val recipeImage: ImageView = findViewById(R.id.recipeImage)
        val kcal: TextView = findViewById(R.id.kcalText)
        val protein: TextView = findViewById(R.id.proteinText)
        val carbs: TextView = findViewById(R.id.carbsText)
        val fat: TextView = findViewById(R.id.fatText)
        val time: TextView = findViewById(R.id.clockText)
        shareButton = findViewById(R.id.shareButton)

        // Configura os valores
        title.text = recipe.title
        val imageResId = recipeImage.context.resources.getIdentifier(
            recipe.image, "drawable", recipeImage.context.packageName
        )
        recipeImage.setImageResource(imageResId)
        kcal.text = recipe.kcal
        protein.text = recipe.protein
        carbs.text = recipe.carbohydrates
        fat.text = recipe.fat
        time.text = "${recipe.time}"

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // RecyclerView de ingredientes
        ingredientsRecyclerView = findViewById(R.id.ingredients)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)

        recipeViewModel.ingredientsOptions.observe(this) { options ->
            options?.let {
                ingredientsAdapter = AdapterIngredients(it, this)
                ingredientsRecyclerView.adapter = ingredientsAdapter
                ingredientsAdapter.startListening()
            }
        }

        // RecyclerView de instruções
        instructionRecyclerView = findViewById(R.id.instructionsRecyclerView)
        instructionRecyclerView.layoutManager = LinearLayoutManager(this)

        // Use o mesmo recipeViewModel aqui (corrigido)
        recipeViewModel.instructionsOptions.observe(this) { options ->
            options?.let {
                instructionAdapter = AdapterInstructions(it)
                instructionRecyclerView.adapter = instructionAdapter
                instructionAdapter.startListening()
            }
        }

        // Link para compartilhamento
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

    override fun onStop() {
        super.onStop()
        if (::ingredientsAdapter.isInitialized) ingredientsAdapter.stopListening()
        if (::instructionAdapter.isInitialized) instructionAdapter.stopListening()
    }
}
