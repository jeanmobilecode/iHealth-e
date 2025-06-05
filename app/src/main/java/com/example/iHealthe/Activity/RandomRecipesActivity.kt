package com.example.iHealthe.Activity

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
import com.example.iHealthe.R
import com.example.iHealthe.RecipeData.RecipeViewModel
import com.example.iHealthe.Adapter.Recipe
import com.example.iHealthe.utils.setupNavigation
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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
    private lateinit var bottomNavigationView: BottomNavigationView
    private val recipeViewModel: RecipeViewModel by viewModels()
    private var mInterstitialAd: InterstitialAd? = null
    private var recipeCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_random_recipes)

        setID()
        setBottomNavigationView()
        observer()
        recipeViewModel.loadRandomRecipe()
        loadInterstitialAd()
        loadNewRecipe()
    }

    private fun loadNewRecipe(){
        newRecipe.setOnClickListener {
            recipeCounter++

            if (recipeCounter % 3 == 0) {
                showInterstitial {
                    recipeViewModel.loadRandomRecipe()
                }
            } else {
                recipeViewModel.loadRandomRecipe()
            }
        }
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
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
    }

    private fun setBottomNavigationView() {
        bottomNavigationView.setupNavigation(R.id.nav_random, this)
    }

    private fun observer() {
        recipeViewModel.randomRecipe.observe(this) { recipe ->
            recipe?.let {
                // Atualiza os campos da UI com os dados da receita
                title.text = it.title
                category.text = it.category
                time.text = "${it.time} min"
                calories.text = "${it.kcal} kcal"
                protein.text = "${it.protein}g"
                carbs.text = "${it.carbohydrates}g"
                fat.text = "${it.fat}g"

                val imageResId = img.context.resources.getIdentifier(it.image, "drawable", img.context.packageName)

                img.setImageResource(imageResId)

                // Clique para compartilhar
                shareButton.setOnClickListener {
                    showInterstitial {
                        shareRecipe(recipe)
                    }
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

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712", // ID de teste do AdMob
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
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    loadInterstitialAd() // Carrega o próximo
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
            mInterstitialAd?.show(this)
        } else {
            afterAdAction() // Se não tiver carregado, segue normalmente
        }
    }

    private fun shareRecipe(recipe: Recipe) {
        val appLink = "https://play.google.com/store/apps/details?id=${packageName}"

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

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_random
    }

}