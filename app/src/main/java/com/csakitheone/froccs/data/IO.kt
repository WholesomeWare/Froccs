package com.csakitheone.froccs.data

import android.content.Context
import com.csakitheone.froccs.R
import com.csakitheone.froccs.model.Ingredient
import com.csakitheone.froccs.model.Recipe
import java.io.BufferedReader
import java.io.InputStreamReader

class IO {
    companion object {

        fun importToDatabase(context: Context) {
            /*val lines = InputStreamReader(context.resources.openRawResource(R.raw.importer)).readLines()

            for (line in lines) {
                val name = line.substringBefore(":")
                val ingredientStrings = line.substringAfter(":").split(",")

                val recipe = Recipe(
                    context,
                    name,
                    ingredientStrings.map {
                        val iName = it.substringAfter("dl").trim()
                        val iAmount = it.substringBefore("dl").trim().toFloat()
                        Ingredient(iName, iAmount, false)
                    }.toMutableList(),
                    false
                )

                FSDB.addRecipe(recipe) {}
            }*/
        }

    }
}