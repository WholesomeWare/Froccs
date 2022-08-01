package com.csakitheone.froccs.model.database

import com.csakitheone.froccs.model.Ingredient

data class IngredientData(
    var name: String? = null,
    var amount: Float? = null
) {
    fun toIngredient(): Ingredient {
        return Ingredient(name ?: "?", amount ?: 0f, false)
    }
}