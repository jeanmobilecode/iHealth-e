package com.example.macroup.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.R
import com.example.macroup.recyclerView.AdapterShopping
import com.example.macroup.recyclerView.Ingredients
import com.example.macroup.sharedPreferences.ShoppingPreferences
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView

class ShoppingActivity : AppCompatActivity() {

    private lateinit var shoppingPreferences: ShoppingPreferences
    private lateinit var shoppingRecyclerView: RecyclerView
    private lateinit var ingredientList : MutableList<Ingredients>
    private lateinit var adapter: AdapterShopping
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var cardRemoveAll: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
        setContentView(R.layout.activity_shopping)

        shoppingPreferences = ShoppingPreferences(this)

        MobileAds.initialize(this){}

        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Recupera a lista salva no SharedPreferences
        ingredientList = shoppingPreferences.getShoppingList()

        // Inicializa o layout vazio
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        cardRemoveAll = findViewById(R.id.cardRemoveAll)

        // Configura RecyclerView
        shoppingRecyclerView = findViewById(R.id.shoppingRecyclerView)
        shoppingRecyclerView.layoutManager = LinearLayoutManager(this)

        // Configura o Adapter e adiciona ao RecyclerView
        adapter = AdapterShopping(ingredientList, this, emptyStateLayout, cardRemoveAll)
        shoppingRecyclerView.adapter = adapter

        // Verifica o estado da lista após inicializar o adapter
        adapter.checkEmptyState()

        // Botão para remover todos os itens
        val cardRemoveAll: CardView = findViewById(R.id.cardRemoveAll)
        cardRemoveAll.setOnClickListener {
            adapter.removeAllItems()
            Toast.makeText(this, "Lista apagada", Toast.LENGTH_SHORT).show()
        }

        setBottomViewNavigation()
    }


    private fun setBottomViewNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_fav

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_fav -> {
                    true
                }

                R.id.nav_search -> {
                    val intent = Intent(this, SearchActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.nav_random -> {
                    val intent = Intent(this, RandomRecipesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.nav_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

}
