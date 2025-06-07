package com.example.iHealthe.Activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iHealthe.Adapter.AdapterRecipe
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.iHealthe.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.iHealthe.RecipeData.RecipeViewModel
import com.example.iHealthe.Adapter.Category
import com.example.iHealthe.Adapter.CategoryAdapter
import com.example.iHealthe.Adapter.Recipe
import com.example.iHealthe.utils.MyApplication
import com.example.iHealthe.utils.setupNavigation
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val recipeViewModel: RecipeViewModel by lazy {
        (application as MyApplication).recipeViewModel
    }

    private lateinit var recipeAdapter: AdapterRecipe
    private lateinit var bottomNavigationView: BottomNavigationView
    private var loadedRecipes: ArrayList<Recipe>? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            recipeViewModel.isRecipesLoaded.value != true
        }
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_main)

        if (recipeViewModel.isRecipesLoaded.value != true) {
            recipeViewModel.loadAllRecipes()
        }

        if (!isInternetAvailable(this)) {
            showNoInternetDialog(this)
        }

        MobileAds.initialize(this){}
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        authentication()
        adView.loadAd(adRequest)
        setupRecyclerView()
        setBottomNavigationView()
        generateRandomRecipe()
        observeRecipes()

    }

    fun authentication(){
        auth = FirebaseAuth.getInstance()

        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("AuthTry", "Login anônimo com UID: ${user?.uid}")
                } else {
                    Log.w("AuthTry", "Falha no login anônimo", task.exception)
                }
            }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun showNoInternetDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.internet_connection))
            .setMessage(getString(R.string.internet_off))
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupRecyclerView() {
        val categoryRecyclerView: RecyclerView = findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val categories = listOf(
            Category(R.string.all_categories, R.drawable.img_all_categories),
            Category(R.string.breakfast, R.drawable.img_breakfast),
            Category(R.string.lunch_and_dinner, R.drawable.img_lunch_and_dinner),
            Category(R.string.high_protein, R.drawable.img_high_protein),
            Category(R.string.post_workout, R.drawable.img_post_workout),
            Category(R.string.pre_workout, R.drawable.img_pre_workout),
            Category(R.string.vegan, R.drawable.img_vegan),
            Category(R.string.vegetarian, R.drawable.img_vegetarian),
            Category(R.string.snack, R.drawable.img_snack),
            Category(R.string.lowcarb, R.drawable.img_lowcarb),
            Category(R.string.fit_dessert, R.drawable.img_fit_dessert)
        )

        val categoryAdapter = CategoryAdapter(categories) { category ->
            val categoryName = getString(category.categoryName)

            if (categoryName == getString(R.string.all_categories)) {
                recipeViewModel.loadAllRecipes()
            } else {
                recipeViewModel.loadRecipesByCategory(categoryName)
            }
        }

        categoryRecyclerView.adapter = categoryAdapter

        val homeRecipesRecyclerView: RecyclerView = findViewById(R.id.homeRecipesRecyclerView)
        homeRecipesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        recipeAdapter = AdapterRecipe(this, mutableListOf())
        homeRecipesRecyclerView.adapter = recipeAdapter
    }

    private fun setBottomNavigationView() {
        bottomNavigationView.setupNavigation(R.id.nav_home, this)
    }

    private fun generateRandomRecipe() {
        val randomRecipeButton: Button = findViewById(R.id.card_view_button)
        randomRecipeButton.setOnClickListener {
            val intent = Intent(this, RandomRecipesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeRecipes() {
        recipeViewModel.recipeList.observe(this) { recipes ->
            if (recipes.isNotEmpty()) {
                loadedRecipes = ArrayList(recipes)
                updateRecipeList(loadedRecipes!!)
            } else {
                Toast.makeText(this, getString(R.string.no_recipe_avaliable), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    private fun updateRecipeList(recipes: ArrayList<Recipe>) {
        recipeAdapter.updateRecipes(recipes)
    }



}















