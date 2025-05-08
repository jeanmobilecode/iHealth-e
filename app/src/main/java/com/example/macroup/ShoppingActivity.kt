package com.example.macroup

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.recyclerView.AdapterShopping
import com.example.macroup.recyclerView.Ingredients
import com.example.macroup.sharedPreferences.ShoppingPreferences
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_shopping)

        shoppingPreferences = ShoppingPreferences(this)

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

        setBottomViewNavegation()
    }

    private fun setBottomViewNavegation(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_fav

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_fav -> true

                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.nav_random -> {
                    startActivity(Intent(this, RandomRecipesActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                else -> false
            }
        }
    }
}
