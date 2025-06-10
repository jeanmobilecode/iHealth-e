package com.logicalayer.ihealthe.SharedPreferences

import android.content.Context
import android.content.SharedPreferences
import com.logicalayer.ihealthe.Adapter.Ingredients
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ShoppingPreferences(context: Context) {

    private val KEY_SHOPPING_LIST = "shopping_list"

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("shopping_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    fun saveShoppingList(newIngredients: List<Ingredients>) {
        val existingList = getShoppingList()
        val updatedList = (existingList + newIngredients).distinctBy { it.name }
        val json = Gson().toJson(updatedList)
        sharedPreferences.edit().putString(KEY_SHOPPING_LIST, json).apply()
    }

    fun getShoppingList(): MutableList<Ingredients> {
        val json = sharedPreferences.getString(KEY_SHOPPING_LIST, "[]")
        val type = object : TypeToken<MutableList<Ingredients>>() {}.type
        return gson.fromJson(json, type)
    }

}
