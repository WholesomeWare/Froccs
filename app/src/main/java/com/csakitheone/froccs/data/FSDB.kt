package com.csakitheone.froccs.data

import android.content.Context
import com.csakitheone.froccs.model.Ingredient
import com.csakitheone.froccs.model.Recipe
import com.csakitheone.froccs.model.database.IngredientData
import com.csakitheone.froccs.model.database.RecipeData
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore

class FSDB {
    companion object {

        private val fsdb = FirebaseFirestore.getInstance()

        fun getRecipes(context: Context, callback: (List<Recipe>) -> Unit) {
            fsdb.collection("recipes").get().addOnCompleteListener {
                if (!it.isSuccessful) {
                    callback(listOf())
                    return@addOnCompleteListener
                }
                callback(it.result.toObjects(RecipeData::class.java).map { rd -> rd.toRecipe(context) })
            }
        }

        fun addRecipe(recipe: Recipe?, callback: (Boolean) -> Unit) {
            if (recipe == null) {
                callback(false)
                return
            }
            val data = RecipeData(
                recipe.name,
                recipe.ingredients.map { IngredientData(it.name, it.amount) }.toMutableList()
            )
            fsdb.collection("recipes").add(data).addOnCompleteListener {
                callback(it.isSuccessful)
            }
        }

    }
}