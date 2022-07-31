package com.csakitheone.froccs.data

import android.app.Activity
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.preference.PreferenceManager
import com.csakitheone.froccs.R
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

    fun createView(activity: Activity, onChangeListener: (ingredientName: String, newAmount: Float) -> Unit) : View {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val v = activity.layoutInflater.inflate(R.layout.layout_ingredient, null, false)
        v.findViewById<TextView>(R.id.ingredientText).text = name
        v.findViewById<SeekBar>(R.id.ingredientSeek).progress = (amount * AMOUNT_PRECISION).toInt()
        v.findViewById<SeekBar>(R.id.ingredientSeek).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val currentPrecision = if (prefs.getBoolean("pref_precise_seekbar", false)) 2 else 1
                amount = progress / AMOUNT_PRECISION.toFloat()
                amount = (amount * currentPrecision).roundToInt() / currentPrecision.toFloat()
                v.findViewById<TextView>(R.id.ingredientText).text = "$name: " +
                        (if (amount == amount.roundToInt().toFloat()) amount.roundToInt() else amount.toString()) +
                        (if (prefs.getBoolean("pref_show_dl", true)) "dl" else "")
                onChangeListener(name, amount)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
        return v
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

    companion object {
        val AMOUNT_PRECISION: Int = 10
    }
}