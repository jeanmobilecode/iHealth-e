package com.example.macroup.recyclerView

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

data class Category(
    val categoryName: Int = 0,
    val categoryIcon: Int = 0,
)

@Parcelize
data class Ingredients(
    val name: String? = null,
    val quantity: String? = null,
    var isChecked: Boolean = false,
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return other is Ingredients &&
                other.name == name &&
                other.quantity == quantity
    }

    override fun hashCode(): Int {
        return (name ?: "").hashCode() * 31 + (quantity ?: "").hashCode()
    }
}

data class Instructions(
    val step: Int = 0,
    val text: String = "",
)


@Parcelize
data class Recipe(
    val randomIndex: Int = (0..1000000).random(),
    val id: String = UUID.randomUUID().toString(),
    val time: String = "",
    val title: String = "",
    val instructions: List<String> = emptyList(),
    val ingredients: List<Ingredients> = emptyList(),
    val category: String = "",
    val image: String = "",
    val kcal: String = "",
    val protein: String = "",
    val carbohydrates: String = "",
    val fat: String = ""
) : Parcelable


