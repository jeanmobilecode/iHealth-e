package com.logicalayer.ihealthe.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.logicalayer.ihealthe.R
import com.google.gson.Gson

class AdapterShopping(
    private var ingredientList: MutableList<Ingredients>,
    private val context: Context,
    private val emptyStateLayout: ConstraintLayout,
    private val cardRemoveAll: CardView
) : RecyclerView.Adapter<AdapterShopping.ShoppingViewHolder>() {

    class ShoppingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientName: TextView = itemView.findViewById(R.id.ingredientName)
        val ingredientQuantity: TextView = itemView.findViewById(R.id.ingredientQuantity)
        val deleteIcon: Button = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shopping, parent, false)
        return ShoppingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val ingredient = ingredientList[position]

        holder.ingredientName.text = ingredient.name
        holder.ingredientQuantity.text = ingredient.quantity

        holder.deleteIcon.setOnClickListener {
            removeItem(ingredient)
        }
    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    fun removeAllItems() {
        if (ingredientList.isNotEmpty()) {
            ingredientList.clear()
            updateShoppingListInPreferences()
            notifyDataSetChanged()
        }
        checkEmptyState()
    }

    fun removeItem(ingredient: Ingredients) {
        val position = ingredientList.indexOf(ingredient)
        if (position != -1) {
            ingredientList.removeAt(position)
            updateShoppingListInPreferences()
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, ingredientList.size)
        }
        checkEmptyState()
    }

    fun checkEmptyState() {
        emptyStateLayout.visibility = if (ingredientList.isEmpty()) View.VISIBLE else View.GONE
        cardRemoveAll.visibility = if (ingredientList.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateShoppingListInPreferences() {
        val sharedPreferences = context.getSharedPreferences("shopping_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val json = gson.toJson(ingredientList)

        editor.putString("shopping_list", json)
        editor.apply()
    }
}
