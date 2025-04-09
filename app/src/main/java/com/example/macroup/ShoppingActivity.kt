package com.example.macroup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var adapter: AdapterShopping

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        setContentView(R.layout.activity_shopping)

        shoppingPreferences = ShoppingPreferences(this)

        // Recupera a lista salva no SharedPreferences
        val ingredientsList = shoppingPreferences.getShoppingList()

        // Configura RecyclerView
        shoppingRecyclerView = findViewById(R.id.shoppingRecyclerView)
        shoppingRecyclerView.layoutManager = LinearLayoutManager(this)

        // Configura o Adapter e adiciona ao RecyclerView
        adapter = AdapterShopping(ingredientsList, this) { ingredient ->
            removeIngredient(ingredient)
        }
        shoppingRecyclerView.adapter = adapter

        setBottomViewNavegation()
    }

    private fun removeIngredient(ingredient: Ingredients) {
        adapter.removeItem(ingredient)
    }

    private fun setBottomViewNavegation(){
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nav_fav

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    //overridePendingTransition(0, 0) Remove animação de transição
                    finish()
                    true
                }
                R.id.nav_fav -> true // Já estamos nessa Activity
                else -> false
            }
        }
    }


}
