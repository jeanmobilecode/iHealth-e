package com.example.macroup.recyclerView

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

data class Category(
    val categoryName: Int = 0,
    val categoryIcon: Int = 0,
)

data class Ingredients(
    val name: String? = null,
    val quantity: String? = null,
    var isChecked: Boolean = false,
)

data class Instructions(
    val step: Int = 0,
    val text: String = "",
)

data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    val time: String = "",
    val title: String = "",
    val instructions: List<String> = emptyList(),
    val ingredients: List<String> = emptyList(),
    val category: String = "",
    val image: Int = 0,
    val kcal: String = "",
    val protein: String = "",
    val carbohydrates: String = "",
    val fat: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: UUID.randomUUID().toString(),
        time = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        instructions = parcel.createStringArrayList() ?: emptyList(),
        ingredients = parcel.createStringArrayList() ?: emptyList(),
        category = parcel.readString() ?: "",
        image = parcel.readInt(),
        kcal = parcel.readString() ?: "",
        protein = parcel.readString() ?: "",
        carbohydrates = parcel.readString() ?: "",
        fat = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(time)
        parcel.writeString(title)
        parcel.writeStringList(instructions)
        parcel.writeStringList(ingredients)
        parcel.writeString(category)
        parcel.writeInt(image)
        parcel.writeString(kcal)
        parcel.writeString(protein)
        parcel.writeString(carbohydrates)
        parcel.writeString(fat)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}

