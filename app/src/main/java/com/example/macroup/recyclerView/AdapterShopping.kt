package com.example.macroup.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.R
import com.google.gson.Gson

class AdapterShopping(
    private var ingredientList: MutableList<Ingredients>,
    private val context: Context,
    private val onDeleteClick: (Ingredients) -> Unit
) : RecyclerView.Adapter<AdapterShopping.ShoppingViewHolder>() {

    class ShoppingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
        val ingredientQuantity: TextView = itemView.findViewById(R.id.ingredientQuantity)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shopping, parent, false)
        return ShoppingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val ingredient = ingredientList[position]
        holder.ingredientName.text = "${ingredient.name}"
        holder.ingredientQuantity.text = "${ingredient.quantity}"

        // Clique no ícone de lixeira para remover o ingrediente
        holder.deleteIcon.setOnClickListener {
            ingredientList.removeAt(position) // Remove da lista na memória
            updateShoppingListInPreferences() // Atualiza o SharedPreferences
            notifyItemRemoved(position) // Atualiza a RecyclerView
            notifyItemRangeChanged(position, ingredientList.size)
        }
    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    fun removeItem(ingredient: Ingredients) {
        val position = ingredientList.indexOf(ingredient)
        if (position != -1) {
            ingredientList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // Atualiza o SharedPreferences após a remoção
    private fun updateShoppingListInPreferences() {
        val sharedPreferences = context.getSharedPreferences("shopping_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val json = gson.toJson(ingredientList) // Converte a lista atualizada para JSON

        editor.putString("shopping_list", json) // Salva a nova lista no SharedPreferences
        editor.apply()
    }
}
