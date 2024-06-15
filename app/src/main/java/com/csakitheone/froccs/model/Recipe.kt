package com.csakitheone.froccs.model

import android.content.Context
import kotlin.math.roundToInt

class Recipe(val context: Context) {
    var name: String = ""
    var englishName: String = ""
    var ingredients: MutableList<Ingredient> = mutableListOf()
    var isRemovable: Boolean = true

    constructor(context: Context, name: String, ingredients: MutableList<Ingredient>, isRemovable: Boolean = true): this(context) {
        this.name = name
        this.ingredients = mutableListOf()
        this.ingredients.addAll(ingredients.map { r -> r.copy() })
        this.isRemovable = isRemovable
    }

    constructor(context: Context, text: String, isRemovable: Boolean = true): this(context) {
        this.name = text.split(':')[0]
        this.ingredients = mutableListOf()
        this.ingredients.addAll(text.split(':')[1].split(',').map { r -> Ingredient(r, false) })
        this.isRemovable = isRemovable
    }

    fun getIngredientsString(): String {
        return ingredients.joinToString { "${it.name}: ${it.amount}dl" }
            .replace(".0", "")
    }

    fun getSize(): Float {
        return ingredients.map { it.amount }.sum()
    }

    fun check(ings: List<Ingredient>): Boolean {
        for (i in ingredients) {
            if (!ings.filter { r -> r.amount > 0 }.contains(i))
                return false
        }
        for (i in ings.filter { r -> r.amount > 0 }) {
            if (!ingredients.contains(i))
                return false
        }
        return true
    }

    fun toSavableString(): String {
        return "$name:${ingredients.joinToString(",")}"
    }

    @Deprecated("Rewrite needed")
    override fun toString(): String {
        /*val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        var text = "$name:\n"
        text += ingredients.joinToString { r ->
            (if (r.amount == r.amount.roundToInt().toFloat()) r.amount.roundToInt() else r.amount).toString() + (if (prefs.getBoolean("pref_show_dl", true)) "dl " else " ") + r.name
        }
        return text.replace("()", "")*/
        return ""
    }

    class Builder(context: Context) {
        private val recipe = Recipe(context)

        fun build(): Recipe {
            return recipe
        }

        fun setName(name: String): Builder {
            recipe.name = name
            return this
        }

        fun setEnglishName(name: String): Builder {
            recipe.englishName = name
            return this
        }

        fun addIngredient(ingredient: Ingredient): Builder {
            val ingredients = recipe.ingredients.toMutableList()
            ingredients.add(ingredient)
            recipe.ingredients = ingredients
            return this
        }

        fun addAllIngredients(ingredients: List<Ingredient>): Builder {
            val ings = recipe.ingredients.toMutableList()
            ingredients.filter { r -> r.amount != 0F }.map { r -> ings.add(r.copy()) }
            recipe.ingredients = ings
            return this
        }
    }
}