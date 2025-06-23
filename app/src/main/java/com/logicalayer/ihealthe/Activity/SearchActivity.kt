package com.logicalayer.ihealthe.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicalayer.ihealthe.Adapter.AdapterRecipe
import com.logicalayer.ihealthe.R
import com.logicalayer.ihealthe.RecipeData.RecipeViewModel
import com.logicalayer.ihealthe.utils.setupNavigation
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.view.ViewGroup.LayoutParams


class SearchActivity : AppCompatActivity() {

    private val recipeViewModel: RecipeViewModel by viewModels()

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: AdapterRecipe
    private lateinit var emptyStateLayout: ConstraintLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchResultsText: TextView
    private lateinit var nestedView: NestedScrollView
    private lateinit var params: LayoutParams

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_search)

        initViews()
        setupRecyclerView()
        setupAds()
        setupSearchView()
        setupQueryListener()
        observeRecipes()
        setupBottomNavigation()
        checkEmptyState()
    }


    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_view)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        searchResultsText = findViewById(R.id.search_results_txt)
        searchView = findViewById(R.id.search_view)
        nestedView = findViewById(R.id.nested_view)
        params = nestedView.layoutParams

    }

    private fun setupRecyclerView() {
        recipeAdapter = AdapterRecipe(this, arrayListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter
    }

    private fun setupAds() {
        MobileAds.initialize(this) {}
        val adView = findViewById<AdView>(R.id.adView)
        adView.loadAd(AdRequest.Builder().build())
    }

    private fun setupSearchView() {
        searchView.apply {
            isIconified = false
            queryHint = getString(R.string.search_hint)

            findViewById<View>(androidx.appcompat.R.id.search_plate)?.setBackgroundColor(Color.TRANSPARENT)

            findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)?.setOnClickListener {
                setQuery("", false)
                clearFocus()
                clearSearchResults()
            }
        }
    }

    private fun setupQueryListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchJob?.cancel()
                val text = query?.trim() ?: ""

                if (text.isNotEmpty()) {
                    performSearch(text)
                } else {
                    clearSearchResults()
                }

                hideKeyboard()
                searchView.clearFocus()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                val text = newText?.trim() ?: ""

                if (text.isEmpty()) {
                    clearSearchResults()
                    return true
                }

                searchJob = lifecycleScope.launch {
                    delay(500)
                    performSearch(text)
                }

                return true
            }
        })
    }

    private fun performSearch(query: String) {
        recipeViewModel.searchRecipes(query)
    }

    private fun observeRecipes() {
        recipeViewModel.recipeList.observe(this) { recipes ->
            recipeAdapter.updateRecipes(ArrayList(recipes))
            val currentQuery = searchView.query.toString().trim()
            checkEmptyState(currentQuery)
        }
    }

    private fun checkEmptyState(query: String = "") {
        val isEmpty = recipeAdapter.itemCount == 0

        if (isEmpty) {
            emptyStateLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            val emptyTitle = findViewById<TextView>(R.id.title_empty_state)
            val emptySubtitle = findViewById<TextView>(R.id.subtitle_empty_state)

            emptySubtitle.text = if(query.isBlank()) {
                getString(R.string.search_for_your_recipes)
            } else {
                getString(R.string.do_a_new_search)
            }

            emptyTitle.text = if (query.isBlank()) {
                getString(R.string.search_something)
            } else {
                getString(R.string.no_recipes_found)
            }

            searchResultsText.text = ""
        } else {
            emptyStateLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            params.height = 0
            searchResultsText.text = getString(R.string.search_results)
        }
    }


    private fun clearSearchResults() {
        recipeAdapter.updateRecipes(arrayListOf())
        checkEmptyState("")
        params.height = LayoutParams.WRAP_CONTENT
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setupNavigation(R.id.nav_search, this)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_search
        searchView.clearFocus()
        searchView.isIconified = true
    }
}
