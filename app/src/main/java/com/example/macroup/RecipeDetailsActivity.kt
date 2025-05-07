package com.example.macroup

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.recyclerView.AdapterIngredients
import com.example.macroup.recyclerView.AdapterInstructions
import com.example.macroup.recyclerView.Recipe

class RecipeDetailsActivity : AppCompatActivity() {

    private val recipeDetailsViewModel: RecipeDetailsViewModel by viewModels()
    private lateinit var ingredientsAdapter: AdapterIngredients
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var instructionAdapter: AdapterInstructions
    private lateinit var instructionRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_recipe_details)

        //Recebe os dados da receita
        val recipe = intent.getParcelableExtra<Recipe>("RECIPE")!!

        // Passando o ID da receita para a ViewModel
        recipeDetailsViewModel.setRecipeId(recipe.id)

        // Recebendo a referência de cada item do layout

        val title: TextView = findViewById(R.id.recipeTitle)
        val recipeImage: ImageView = findViewById(R.id.recipeImage)
        val kcal : TextView = findViewById(R.id.kcalText)
        val protein : TextView = findViewById(R.id.proteinText)
        val carbs : TextView = findViewById(R.id.carbsText)
        val fat: TextView = findViewById(R.id.fatText)
        val time: TextView = findViewById(R.id.clockText)


        // Adicionando um valor a essas referências
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

        //toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "" // opcional

        // Habilita a seta de voltar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Configuração da RecyclerView de ingredientes
        ingredientsRecyclerView = findViewById(R.id.ingredients)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)

        recipeDetailsViewModel.recipeId.observe(this) { id ->
            if (id != null) {
                val options = recipeDetailsViewModel.getIngredientsOptions()
                if (options != null) {
                    ingredientsAdapter = AdapterIngredients(options, this)
                    ingredientsRecyclerView.adapter = ingredientsAdapter
                    ingredientsAdapter.startListening()
                }
            }
        }

        // Configuração da RecyclerView de instruções
        instructionRecyclerView = findViewById(R.id.instructionsRecyclerView)
        instructionRecyclerView.layoutManager = LinearLayoutManager(this)

        recipeDetailsViewModel.recipeId.observe(this) { id ->
            if (id != null) {
                val instructionsOptions = recipeDetailsViewModel.getInstructionsOptions()
                if (instructionsOptions != null) {
                    instructionAdapter = AdapterInstructions(instructionsOptions)
                    instructionRecyclerView.adapter = instructionAdapter
                    instructionAdapter.startListening()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStop() {
        super.onStop()
        ingredientsAdapter.stopListening()
        instructionAdapter.stopListening()
    }
}




