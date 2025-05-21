package com.example.macroup.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.example.macroup.R
import com.example.macroup.RecipeData.RecipeViewModel
import com.example.macroup.recyclerView.Recipe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_splash)

        val lottieView = findViewById<LottieAnimationView>(R.id.lottieAnimation)
        val title = findViewById<TextView>(R.id.title)

        // Pré-carrega a composição Lottie
        LottieCompositionFactory.fromRawRes(this, R.raw.animation3).addListener { composition ->
            lottieView.setComposition(composition)
            lottieView.playAnimation()
        }

        // Inicia carregamento após animação iniciar
        recipeViewModel.isRecipesLoaded.observe(this) { loaded ->
            if (loaded == true) {
                title.text = getString(R.string.all_ready)
                lifecycleScope.launch {
                    delay(1500L) // Pequeno delay para ver a animação
                    val recipes = ArrayList(recipeViewModel.recipeList.value ?: emptyList())
                    navigateToMain(recipes)
                }
            }
        }

        // Começa o carregamento só depois de iniciar animação para não travar UI
        recipeViewModel.loadAllRecipes()
    }

    private fun navigateToMain(recipes: ArrayList<Recipe>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putParcelableArrayListExtra("RECIPES", recipes)
        startActivity(intent)
        finish()
    }
}
