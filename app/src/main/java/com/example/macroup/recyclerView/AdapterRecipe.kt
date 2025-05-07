package com.example.macroup.recyclerView

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.macroup.R
import com.example.macroup.RecipeDetailsActivity



class AdapterRecipe(private val context: Context, var recipeList: MutableList<Recipe>) : RecyclerView.Adapter<AdapterRecipe.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val recipeTime: TextView = itemView.findViewById(R.id.recipeTime)
        val recipeCategory: TextView = itemView.findViewById(R.id.recipeCategory)
        val recipeCalories: TextView = itemView.findViewById(R.id.recipeCalories)
        val protein: TextView = itemView.findViewById(R.id.descriptionProtein)
        val carbs: TextView = itemView.findViewById(R.id.descriptionCarbs)
        val fat: TextView = itemView.findViewById(R.id.descriptionFat)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recipes_home, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.recipeTitle.text = recipe.title
        holder.recipeTime.text = "${recipe.time} min"
        holder.recipeCategory.text = recipe.category
        holder.recipeCalories.text = "${recipe.kcal} kcal"
        holder.protein.text = recipe.protein
        holder.carbs.text = recipe.carbohydrates
        holder.fat.text = recipe.fat

        val imageResId = holder.itemView.context.resources.getIdentifier(
            recipe.image, "drawable", holder.itemView.context.packageName
        )
        holder.recipeImage.setImageResource(imageResId)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecipeDetailsActivity::class.java)
            intent.putExtra("RECIPE", recipe)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }


    fun updateList(newList: MutableList<Recipe>) {
        Log.i("log_updateList", "Atualizando lista com ${newList.size} receitas")
        recipeList.clear() // Garante que a lista anterior seja apagada
        recipeList.addAll(newList) // Adiciona os novos itens corretamente
        notifyDataSetChanged() // Atualiza a RecyclerView
    }
}


