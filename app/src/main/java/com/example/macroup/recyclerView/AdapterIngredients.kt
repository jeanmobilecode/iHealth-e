package com.example.macroup.recyclerView

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.R
import com.example.macroup.sharedPreferences.ShoppingPreferences
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class AdapterIngredients(options: FirestoreRecyclerOptions<Ingredients>, private val context: Context)
    : FirestoreRecyclerAdapter<Ingredients, AdapterIngredients.IngredientViewHolder>(options) {

    private val selectedIngredients = mutableListOf<Ingredients>() // Lista de ingredientes selecionados

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxIngredient)
        val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int, model: Ingredients) {
        holder.ingredientName.text = "${model.quantity} ${model.name}"

        // Instancia SharedPreferences
        val shoppingPreferences = ShoppingPreferences(holder.itemView.context)

        // Atualiza o estado do CheckBox
        holder.checkBox.isChecked = selectedIngredients.contains(model) //retorna um booleano

        // Adiciona ou remove ingrediente ao selecionar o CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedIngredients.add(model)
            } else {
                selectedIngredients.remove(model)
            }

            // Salva a lista no SharedPreferences
            shoppingPreferences.saveShoppingList(selectedIngredients)

            Log.i("IngredientAdapter", "Selecionados: $selectedIngredients")
        }
    }
}
