package com.logicalayer.ihealthe.Activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.logicalayer.ihealthe.R
import com.logicalayer.ihealthe.RecipeData.RecipeViewModel
import com.logicalayer.ihealthe.Adapter.Recipe
import com.logicalayer.ihealthe.utils.setupNavigation
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.bottomnavigation.BottomNavigationView

class RandomRecipesActivity : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var title: TextView
    private lateinit var category: TextView
    private lateinit var time: TextView
    private lateinit var calories: TextView
    private lateinit var protein: TextView
    private lateinit var carbs: TextView
    private lateinit var fat: TextView
    private lateinit var newRecipe: Button
    private lateinit var cardView: CardView
    private lateinit var shareButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var progressBar: ProgressBar


    private val recipeViewModel: RecipeViewModel by viewModels()

    private var mInterstitialAd: InterstitialAd? = null
    private var recipeCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_random_recipes)

        initializeViews()
        setBottomNavigationView()
        setupObserver()

        showLoading(true)
        recipeViewModel.loadRandomRecipe()

        loadInterstitialAd()
        handleNewRecipeButton()


    }


    private fun initializeViews() {
        img = findViewById(R.id.recipeImage)
        title = findViewById(R.id.recipeTitle)
        category = findViewById(R.id.recipeCategory)
        time = findViewById(R.id.recipeTime)
        calories = findViewById(R.id.recipeCalories)
        protein = findViewById(R.id.descriptionProtein)
        carbs = findViewById(R.id.descriptionCarbs)
        fat = findViewById(R.id.descriptionFat)
        newRecipe = findViewById(R.id.generateButton)
        cardView = findViewById(R.id.card_view)
        shareButton = findViewById(R.id.shareButton)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setBottomNavigationView() {
        bottomNavigationView.setupNavigation(R.id.nav_random, this)
    }

    private fun setupObserver() {
        recipeViewModel.randomRecipe.observe(this) { recipe ->
            recipe?.let {
                showLoading(false)

                title.text = it.title
                category.text = it.category
                time.text = "${it.time} min"
                calories.text = "${it.kcal} kcal"
                protein.text = "${it.protein}g"
                carbs.text = "${it.carbohydrates}g"
                fat.text = "${it.fat}g"

                val imageResId = resources.getIdentifier(it.image, "drawable", packageName)
                img.setImageResource(imageResId)

                shareButton.setOnClickListener {
                    showInterstitial { shareRecipe(recipe) }
                }

                cardView.setOnClickListener {
                    recipeViewModel.setCurrentRecipeId(recipe.id)
                    startActivity(Intent(this, RecipeDetailsActivity::class.java).apply {
                        putExtra("RECIPE", recipe)
                    })
                }
            }
        }
    }

    private fun handleNewRecipeButton() {
        newRecipe.setOnClickListener {
            recipeCounter++
            showLoading(true)

            if (recipeCounter % 3 == 0) {
                showInterstitial { recipeViewModel.loadRandomRecipe() }
            } else {
                recipeViewModel.loadRandomRecipe()
            }
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-9589405733905435/1449274421",
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

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        cardView.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        newRecipe.isEnabled = !isLoading
        shareButton.isEnabled = !isLoading
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_random
    }
}
