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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class AdapterIngredients(
    options: FirestoreRecyclerOptions<Ingredients>,
    private val context: Context
) : FirestoreRecyclerAdapter<Ingredients, AdapterIngredients.IngredientViewHolder>(options) {

    private val selectedIngredients = mutableListOf<Ingredients>()

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxIngredient)
        val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int, model: Ingredients) {
        holder.ingredientName.text = "${model.quantity} ${model.name}"

        val shoppingPreferences = ShoppingPreferences(holder.itemView.context)

        // Carrega lista salva do SharedPreferences
        val savedList = shoppingPreferences.getShoppingList()

        // Remove listener anterior
        holder.checkBox.setOnCheckedChangeListener(null)

        // Verifica se o item está salvo
        val isChecked = savedList.contains(model)
        holder.checkBox.isChecked = isChecked

        // Atualiza visual
        if (isChecked) {
            if (!selectedIngredients.contains(model)) selectedIngredients.add(model)
            holder.checkBox.setBackgroundResource(R.drawable.icon_checked_checkbox)
        } else {
            selectedIngredients.remove(model)
            holder.checkBox.setBackgroundResource(R.drawable.icon_unchecked_checkbox)
        }

        // Listener
        holder.checkBox.setOnCheckedChangeListener { _, isNowChecked ->
            if (isNowChecked) {
                if (!selectedIngredients.contains(model)) selectedIngredients.add(model)
                holder.checkBox.setBackgroundResource(R.drawable.icon_checked_checkbox)

                // Notificar o usuário que o ingrediente foi adicionado
                Toast.makeText(holder.itemView.context, "${model.name} adicionado à lista de compras", Toast.LENGTH_SHORT).show()
            } else {
                selectedIngredients.remove(model)
                holder.checkBox.setBackgroundResource(R.drawable.icon_unchecked_checkbox)

                // Notificar o usuário que o ingrediente foi removido
                Toast.makeText(holder.itemView.context, "${model.name} removido da lista de compras", Toast.LENGTH_SHORT).show()
            }

            shoppingPreferences.saveShoppingList(selectedIngredients)
            Log.i("IngredientAdapter", "Selecionados: $selectedIngredients")
        }
    }

}
