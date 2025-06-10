package com.logicalayer.ihealthe.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.logicalayer.ihealthe.R
import com.logicalayer.ihealthe.SharedPreferences.ShoppingPreferences

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

        holder.checkBox.setOnCheckedChangeListener(null)

        val isChecked = ingredient in selectedIngredients
        holder.checkBox.isChecked = isChecked
        updateCheckboxVisual(holder, isChecked)

        holder.checkBox.setOnCheckedChangeListener { _, isNowChecked ->
            if (isNowChecked) {
                if (!selectedIngredients.contains(ingredient)) {
                    selectedIngredients.add(ingredient)
                }
                updateCheckboxVisual(holder, true)
                Toast.makeText(context, "${ingredient.name} ${context.getString(R.string.added_to_shopping)}", Toast.LENGTH_SHORT).show()
            } else {
                selectedIngredients.remove(ingredient)
                updateCheckboxVisual(holder, false)
                Toast.makeText(context, "${ingredient.name} ${R.string.removed_of_shopping}", Toast.LENGTH_SHORT).show()
            }

            shoppingPreferences.saveShoppingList(selectedIngredients)
        }
    }

    override fun getItemCount(): Int = ingredientsList.size

    fun updateList(newList: List<Ingredients>) {
        ingredientsList = newList
        notifyDataSetChanged()
    }

    private fun updateCheckboxVisual(holder: IngredientViewHolder, isChecked: Boolean) {
        val backgroundRes = if (isChecked) {
            R.drawable.icon_checked_checkbox
        } else {
            R.drawable.icon_unchecked_checkbox
        }
        holder.checkBox.setBackgroundResource(backgroundRes)
    }
}
