package com.csakitheone.froccs.model.database

import android.content.Context
import androidx.annotation.Keep
import com.csakitheone.froccs.model.Recipe

@Keep
data class RecipeData(
    var name: String? = null,
    var ingredients: MutableList<IngredientData>? = null
) {
    fun toRecipe(context: Context): Recipe {
        return Recipe(
            context,
            name ?: "?",
            ingredients?.map { it.toIngredient() }?.toMutableList() ?: mutableListOf(),
            false
        )
    }
}