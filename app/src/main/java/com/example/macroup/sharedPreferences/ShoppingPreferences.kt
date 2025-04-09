package com.example.macroup.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import com.example.macroup.recyclerView.Ingredients
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ShoppingPreferences(context: Context) {

    private val KEY_SHOPPING_LIST = "shopping_list"

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("shopping_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    fun saveShoppingList(newIngredients: List<Ingredients>) {
        val existingList = getShoppingList()// Recupera a lista atual
        val updatedList = (existingList + newIngredients).distinctBy { it.name } // Evita duplicatas
        val json = Gson().toJson(updatedList)
        sharedPreferences.edit().putString(KEY_SHOPPING_LIST, json).apply()
    }

    fun getShoppingList(): MutableList<Ingredients> {
        val json = sharedPreferences.getString("shopping_list", "[]")
        val type = object : TypeToken<MutableList<Ingredients>>() {}.type
        return gson.fromJson(json, type)
    }

}
