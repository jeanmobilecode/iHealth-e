package com.example.macroup.recyclerView

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.R
import com.example.macroup.sharedPreferences.ShoppingPreferences

class AdapterIngredients(
    private val context: Context,
    private var ingredientsList: List<Ingredients>
) : RecyclerView.Adapter<AdapterIngredients.IngredientViewHolder>() {

    private val selectedIngredients = mutableListOf<Ingredients>()
    private val shoppingPreferences = ShoppingPreferences(context)

    init {
        selectedIngredients.addAll(shoppingPreferences.getShoppingList())
    }

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxIngredient)
        val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredientsList[position]
        holder.ingredientName.text = "${ingredient.quantity} ${ingredient.name}"

        // Remove listener anterior
        holder.checkBox.setOnCheckedChangeListener(null)

        // Verifica se o item está salvo na lista
        val isChecked = selectedIngredients.contains(ingredient)
        holder.checkBox.isChecked = isChecked
        updateCheckboxVisual(holder, isChecked)

        // Listener do CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isNowChecked ->
            if (isNowChecked) {
                if (!selectedIngredients.contains(ingredient)) {
                    selectedIngredients.add(ingredient)
                }
                updateCheckboxVisual(holder, true)

                Toast.makeText(context, "${ingredient.name} adicionado à lista de compras", Toast.LENGTH_SHORT).show()
            } else {
                selectedIngredients.remove(ingredient)
                updateCheckboxVisual(holder, false)

                Toast.makeText(context, "${ingredient.name} removido da lista de compras", Toast.LENGTH_SHORT).show()
            }

            // Salva no SharedPreferences
            shoppingPreferences.saveShoppingList(selectedIngredients)
            Log.i("IngredientAdapter", "Selecionados: $selectedIngredients")
        }
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    fun updateList(newList: List<Ingredients>) {
        ingredientsList = newList
        notifyDataSetChanged()
    }

    private fun updateCheckboxVisual(holder: IngredientViewHolder, isChecked: Boolean) {
        if (isChecked) {
            holder.checkBox.setBackgroundResource(R.drawable.icon_checked_checkbox)
        } else {
            holder.checkBox.setBackgroundResource(R.drawable.icon_unchecked_checkbox)
        }
    }
}
