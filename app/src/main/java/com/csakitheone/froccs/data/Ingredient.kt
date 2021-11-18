package com.csakitheone.froccs.data

import android.app.Activity
import android.view.View
import android.widget.SeekBar
import androidx.preference.PreferenceManager
import com.csakitheone.froccs.R
import kotlinx.android.synthetic.main.layout_ingredient.view.*
import kotlin.math.abs
import kotlin.math.roundToInt

class Ingredient() {
    var name: String = ""
    var amount: Float = 0F
    var isRemovable: Boolean = true

    constructor(name: String, amount: Float, isRemovable: Boolean = true) : this() {
        this.name = name
        this.amount = amount
        this.isRemovable = isRemovable
    }

    constructor(text: String, isRemovable: Boolean = true) : this() {
        this.name = text.split('-')[0]
        if (text.contains('-')) {
            this.amount = text.split('-')[1].toFloat()
        }
        this.isRemovable = true
    }

    fun createView(activity: Activity, onChangeListener: (ingredientName: String, newAmount: Float) -> Unit) : View {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val v = activity.layoutInflater.inflate(R.layout.layout_ingredient, null, false)
        v.ingredientText.text = name
        v.ingredientSeek.progress = (amount * AMOUNT_PRECISION).toInt()
        v.ingredientSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                amount = progress / AMOUNT_PRECISION.toFloat()
                amount = (amount * AMOUNT_USER_PRECISION).roundToInt() / AMOUNT_USER_PRECISION.toFloat()
                v.ingredientText.text = "$name: " +
                        (if (amount == amount.roundToInt().toFloat()) amount.roundToInt() else amount.toString()) +
                        (if (prefs.getBoolean("pref_show_dl", true)) "dl" else "")
                onChangeListener(name, amount)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
        v.ingredientBtnRemove.visibility = if (isRemovable) View.VISIBLE else View.GONE
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

    companion object {
        val AMOUNT_PRECISION: Int = 10
        val AMOUNT_USER_PRECISION: Int = 2
    }
}