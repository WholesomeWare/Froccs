package com.csakitheone.froccs.model

import kotlin.math.abs
import kotlin.math.roundToInt

class Ingredient() {
    var name: String = ""
    var amount: Float = 0f
    var isRemovable: Boolean = true

    constructor(name: String, amount: Float = 0f, isRemovable: Boolean) : this() {
        this.name = name
        this.amount = amount
        this.isRemovable = isRemovable
    }

    constructor(text: String, isRemovable: Boolean) : this() {
        this.name = text.split('-')[0]
        if (text.contains('-')) {
            this.amount = text.split('-')[1].toFloat()
        }
        this.isRemovable = isRemovable
    }

    fun copy() : Ingredient {
        return Ingredient(name, amount, isRemovable)
    }

    override fun equals(other: Any?): Boolean {
        return other is Ingredient && other.name == name && (other.amount == amount || other.amount + amount != abs(other.amount) + abs(amount))
    }

    override fun toString(): String {
        return "$name-${(if (amount == amount.roundToInt().toFloat()) amount.roundToInt() else amount.toString())}"
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + isRemovable.hashCode()
        return result
    }
}