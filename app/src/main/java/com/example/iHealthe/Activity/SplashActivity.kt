package com.example.iHealthe.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.example.iHealthe.R
import com.example.iHealthe.RecipeData.RecipeViewModel
import com.example.iHealthe.utils.MyApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val recipeViewModel: RecipeViewModel by lazy {
        (application as MyApplication).recipeViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_splash)

        val lottieView = findViewById<LottieAnimationView>(R.id.lottieAnimation)
        val title = findViewById<TextView>(R.id.title)

        lottieView.playAnimation()

        lifecycleScope.launch {
            delay(250) // Garante que a UI aparece antes de carregar os dados
            recipeViewModel.loadAllRecipes()
        }

        recipeViewModel.isRecipesLoaded.observe(this) { loaded ->
            if (loaded == true) {
                title.text = getString(R.string.all_ready)
                lifecycleScope.launch {
                    delay(1500)
                    navigateToMain()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
}

