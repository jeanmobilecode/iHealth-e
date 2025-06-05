package com.example.iHealthe.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
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

        shoppingPreferences = ShoppingPreferences(this)

        ingredientList = shoppingPreferences.getShoppingList()

        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        cardRemoveAll = findViewById(R.id.cardRemoveAll)

        shoppingRecyclerView = findViewById(R.id.shoppingRecyclerView)
        shoppingRecyclerView.layoutManager = LinearLayoutManager(this)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        adapter = AdapterShopping(ingredientList, this, emptyStateLayout, cardRemoveAll)
        shoppingRecyclerView.adapter = adapter

        adsSetup()

        adapter.checkEmptyState()

        removeAllIngredients()

        setBottomNavigationView()
    }

    private fun adsSetup() {
        MobileAds.initialize(this) {}

        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun removeAllIngredients() {
        val cardRemoveAll: CardView = findViewById(R.id.cardRemoveAll)
        cardRemoveAll.setOnClickListener {
            adapter.removeAllItems()
            Toast.makeText(this, "Lista apagada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setBottomNavigationView() {
        bottomNavigationView.setupNavigation(R.id.nav_shopping, this)
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_shopping
    }

}
