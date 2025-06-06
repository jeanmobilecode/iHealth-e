package com.example.iHealthe.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iHealthe.R
import com.example.iHealthe.Adapter.AdapterShopping
import com.example.iHealthe.Adapter.Ingredients
import com.example.iHealthe.SharedPreferences.ShoppingPreferences
import com.example.iHealthe.utils.setupNavigation
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView

class ShoppingActivity : AppCompatActivity() {

    private lateinit var shoppingPreferences: ShoppingPreferences
    private lateinit var shoppingRecyclerView: RecyclerView
    private lateinit var ingredientList: MutableList<Ingredients>
    private lateinit var adapter: AdapterShopping
    private lateinit var emptyStateLayout: ConstraintLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var cardRemoveAll: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_shopping)

        initViews()
        setupRecyclerView()
        setupAds()
        setupRemoveAll()
        setupBottomNavigation()

        adapter.checkEmptyState()
    }

    private fun initViews() {
        shoppingPreferences = ShoppingPreferences(this)
        ingredientList = shoppingPreferences.getShoppingList()

        shoppingRecyclerView = findViewById(R.id.shoppingRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        cardRemoveAll = findViewById(R.id.cardRemoveAll)
    }

    private fun setupRecyclerView() {
        adapter = AdapterShopping(ingredientList, this, emptyStateLayout, cardRemoveAll)
        shoppingRecyclerView.layoutManager = LinearLayoutManager(this)
        shoppingRecyclerView.adapter = adapter
    }

    private fun setupAds() {
        MobileAds.initialize(this) {}
        val adView = findViewById<AdView>(R.id.adView)
        adView.loadAd(AdRequest.Builder().build())
    }

    private fun setupRemoveAll() {
        cardRemoveAll.setOnClickListener {
            adapter.removeAllItems()
            Toast.makeText(this, getString(R.string.list_cleared), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setupNavigation(R.id.nav_shopping, this)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_shopping
    }
}
