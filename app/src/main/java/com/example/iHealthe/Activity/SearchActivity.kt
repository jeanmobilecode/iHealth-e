package com.example.iHealthe.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iHealthe.Adapter.AdapterRecipe
import com.example.iHealthe.R
import com.example.iHealthe.RecipeData.RecipeViewModel
import com.example.iHealthe.utils.setupNavigation
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: AdapterRecipe
    private lateinit var emptyStateLayout: ConstraintLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private var selectedCategory: String = "Todas"
    private var searchJob: Job? = null

    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recycler_view)
        recipeAdapter = AdapterRecipe(this, arrayListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        chipGroupSetup()
        adsSetup()
        searchViewSetup()
        queryTextListener()
        setBottomNavigationView()
        checkEmptyState()
        observeRecipes()
    }

    private fun checkEmptyState() {
        val isEmpty = recipeAdapter.itemCount == 0
        emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun performSearch(query: String, category: String) {
        recipeViewModel.searchRecipes(query, category)
    }

    private fun chipGroupSetup() {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)

        val chipTodas = chipGroup.findViewWithTag<Chip>("Todas")
        chipTodas?.isChecked = true

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            selectedCategory = chip?.text?.toString() ?: "Todas"

            val query = searchView.query.toString().trim()

            if (query.isNotEmpty()) {
                performSearch(query, selectedCategory)
            } else {
                recipeAdapter.updateRecipes(arrayListOf())
                checkEmptyState()
            }
        }
    }

    private fun adsSetup() {
        MobileAds.initialize(this) {}
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun searchViewSetup() {
        searchView = findViewById(R.id.search_view)
        searchView.isIconified = false
        searchView.queryHint = getString(R.string.search_hint)

        val plate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        plate?.setBackgroundColor(Color.TRANSPARENT)

        val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        closeButton.setOnClickListener {
            searchView.setQuery("", false)
            searchView.clearFocus()

            recipeAdapter.updateRecipes(arrayListOf())
            checkEmptyState()
        }
    }

    private fun queryTextListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchJob?.cancel()

                val text = query?.trim() ?: ""
                if (text.isNotEmpty()) {
                    performSearch(text, selectedCategory)
                } else {
                    recipeAdapter.updateRecipes(arrayListOf())
                    checkEmptyState()
                }


                hideKeyboard()
                searchView.clearFocus()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()

                val text = newText?.trim() ?: ""

                if (text.isEmpty()) {
                    recipeAdapter.updateRecipes(arrayListOf())
                    checkEmptyState()
                    return true
                }

                searchJob = lifecycleScope.launch {
                    delay(500)
                    performSearch(text, selectedCategory)
                }

                return true
            }
        })
    }

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    private fun observeRecipes() {
        recipeViewModel.recipeList.observe(this) { recipes ->
            recipeAdapter.updateRecipes(ArrayList(recipes))
            checkEmptyState()
        }
    }

    private fun setBottomNavigationView() {
        bottomNavigationView.setupNavigation(R.id.nav_search, this)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_search
        searchView.clearFocus()
        searchView.isIconified = true
    }
}
