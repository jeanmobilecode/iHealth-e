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
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class RecipeDetailsActivity : AppCompatActivity() {

    private val recipeViewModel: RecipeViewModel by viewModels()

    private lateinit var ingredientsAdapter: AdapterIngredients
    private lateinit var ingredientsRecyclerView: RecyclerView

    private lateinit var instructionAdapter: AdapterInstructions
    private lateinit var instructionRecyclerView: RecyclerView

    private lateinit var shareButton: Button
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_recipe_details)

        initializeAds()
        loadInterstitialAd()
        setupToolbar()

        val recipe = intent.getParcelableExtra<Recipe>("RECIPE") ?: return
        recipeViewModel.setCurrentRecipeId(recipe.id)

        setRecipeInfo(recipe)
        setupIngredients()
        setupInstructions()
        setupShareButton(recipe)
    }

    private fun initializeAds() {
        MobileAds.initialize(this) {}
        findViewById<AdView>(R.id.adView).loadAd(AdRequest.Builder().build())
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-9589405733905435/4940344176",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    mInterstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }
            }
        )
    }

    private fun showInterstitial(afterAdAction: () -> Unit) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                loadInterstitialAd()
                afterAdAction()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
                loadInterstitialAd()
                afterAdAction()
            }

            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null
            }
        }

        mInterstitialAd?.show(this) ?: afterAdAction()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setRecipeInfo(recipe: Recipe) {
        findViewById<TextView>(R.id.recipeTitle).text = recipe.title
        findViewById<TextView>(R.id.kcalText).text = "${recipe.kcal} kcal"
        findViewById<TextView>(R.id.proteinText).text = "${recipe.protein}g"
        findViewById<TextView>(R.id.carbsText).text = "${recipe.carbohydrates}g"
        findViewById<TextView>(R.id.fatText).text = "${recipe.fat}g"
        findViewById<TextView>(R.id.clockText).text = "${recipe.time} min"

        val imageResId = resources.getIdentifier(recipe.image, "drawable", packageName)
        findViewById<ImageView>(R.id.recipeImage).setImageResource(imageResId)
    }

    private fun setupInstructions() {
        instructionRecyclerView = findViewById(R.id.instructionsRecyclerView)
        instructionRecyclerView.layoutManager = LinearLayoutManager(this)
        instructionAdapter = AdapterInstructions(emptyList())
        instructionRecyclerView.adapter = instructionAdapter

        recipeViewModel.instructionsList.observe(this) { instructions ->
            instructionAdapter.updateList(instructions)
        }
    }

    private fun setupIngredients() {
        ingredientsRecyclerView = findViewById(R.id.ingredients)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        ingredientsAdapter = AdapterIngredients(this, emptyList())
        ingredientsRecyclerView.adapter = ingredientsAdapter

        recipeViewModel.ingredientsList.observe(this) { ingredients ->
            ingredientsAdapter.updateList(ingredients)
        }
    }

    private fun setupShareButton(recipe: Recipe) {
        shareButton = findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            showInterstitial { shareRecipe(recipe) }
        }
    }

    private fun shareRecipe(recipe: Recipe) {
        val appLink = "https://play.google.com/store/apps/details?id=$packageName"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.explore_recipes))
            putExtra(
                Intent.EXTRA_TEXT,
                """
                ${recipe.title}

                ${getString(R.string.category)} ${recipe.category}
                ${getString(R.string.preparation_time)} ${recipe.time} min

                ${getString(R.string.macros_per_serving)}
                - ${getString(R.string.protein)} ${recipe.protein}g
                - ${getString(R.string.carbohydrates)}: ${recipe.carbohydrates}g
                - ${getString(R.string.fat)}: ${recipe.fat}g

                ${getString(R.string.explore_ingredients_instructions)}
                $appLink
            """.trimIndent()
            )
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_on)))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
