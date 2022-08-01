package com.csakitheone.froccs.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.csakitheone.froccs.R
import com.csakitheone.froccs.model.Ingredient
import com.csakitheone.froccs.model.Recipe

class Data {
    companion object {
        private var ingredients: MutableList<Ingredient> = mutableListOf()
        private var recipes: MutableList<Recipe> = mutableListOf()
        private lateinit var context: Context

        private fun loadFirst() {
            ingredients = mutableListOf()
            ingredients.add(Ingredient(name = context.getString(R.string.ingredient_vine), isRemovable = false))
            ingredients.add(Ingredient(name = context.getString(R.string.ingredient_soda), isRemovable = false))
            recipes = mutableListOf()
            recipes.add(Recipe(context, context.getString(R.string.empty_bottle), mutableListOf(), false))
            addSplatter("Kisfröccs", 1F, 1F)
            addSplatter("Nagyfröccs", 2F, 1F)
            addSplatter("Hosszúlépés", 1F, 2F)
            addSplatter("Háziúr / Bivalycsók", 4F, 1F)
            addSplatter("Házmester", 3F, 2F)
            addSplatter("Viceházmester", 2F, 3F)
            addSplatter("Sportfröccs", 1F, 4F)
            addSplatter("Krúdy fröccs", 9F, 1F)
            addSplatter("Avasi fröccs", 7F, 3F)
            addSplatter("Polgármester", 6F, 4F)
            addSplatter("Maflás", 5F, 5F)
            addSplatter("Alpolgármester", 4F, 6F)
            addSplatter("Sóherfröccs / Távolugrás", 1F, 9F)

            addSplatter("Kisharapás (Cecéről)", .5F, .5F)
            addSplatter("Háp-háp (Fehérvárról)", 2F, 2F)
            addSplatter("Előrelépés", 8F, 1F)
            addSplatter("Puskás fröccs / Magyar-angol", 6F, 3F)
            addSplatter("Góré föccs (pohár bor és egy spriccentésnyi szóda)", 2F, .5F)
            addSplatter("Deák föccs (pohár szóda és egy csepp bor)", .5F, 2F)
            addSplatter("Ijesztett / Spricc föccs (sok bor és egy spriccentésnyi szóda)", 9.5F, .5F)

            addSplatter("Csatos", 10F, 5F)
        }

        fun loadUserData(context: Context) {
            this.context = context.applicationContext
            loadFirst()
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            ingredients.addAll(prefs.getStringSet("ingredients", setOf())
                ?.map { r -> Ingredient(r, true) } ?: listOf())
            for (ing in ingredients) {
                ing.amount = 0F
            }
            recipes.addAll(prefs.getStringSet("recipes", setOf())
                ?.map { r -> Recipe(context, r) } ?: listOf())
        }

        fun getIngredients() : List<Ingredient> {
            return ingredients
        }

        fun addIngredient(context: Context, ingredientName: String) {
            ingredients.add(Ingredient(name = ingredientName, isRemovable = true))
            save(context)
        }

        fun removeIngredient(context: Context, ingredientName: String) {
            ingredients.removeAll { it.name == ingredientName }
            save(context)
        }

        fun getRecipes() : List<Recipe> {
            return recipes
        }

        fun addRecipe(context: Context, recipe: Recipe) {
            recipes.add(recipe)
            save(context)
        }

        fun removeRecipe(context: Context, recipe: Recipe) {
            recipes.remove(recipe)
            save(context)
        }

        private fun addSplatter(name: String, wine: Float, soda: Float) {
            recipes.add(Recipe(context, name, mutableListOf(Ingredient(context.getString(R.string.ingredient_vine), wine, false), Ingredient(context.getString(R.string.ingredient_soda), soda, false)), false))
        }

        private fun save(context: Context) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit().apply {
                putStringSet("ingredients", ingredients.filter { r -> r.isRemovable }.map { r -> r.toString() }.toSet())
                putStringSet("recipes", recipes.filter { r -> r.isRemovable }.map { r -> r.toSavableString() }.toSet())
                apply()
            }
        }
    }
}