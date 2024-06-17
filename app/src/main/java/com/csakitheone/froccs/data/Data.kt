package com.csakitheone.froccs.data

import android.content.Context
import com.csakitheone.froccs.R
import com.csakitheone.froccs.model.Ingredient
import com.csakitheone.froccs.model.Recipe
import kotlin.math.roundToInt

class Data {
    companion object {
        private var ingredients: MutableList<Ingredient> = mutableListOf()
        private var recipes: MutableList<Recipe> = mutableListOf()

        fun load(context: Context) {
            ingredients = mutableListOf()
            ingredients.add(
                Ingredient(
                    name = context.getString(R.string.ingredient_vine),
                    isRemovable = false
                )
            )
            ingredients.add(
                Ingredient(
                    name = context.getString(R.string.ingredient_soda),
                    isRemovable = false
                )
            )
            recipes = mutableListOf()
            recipes.add(
                Recipe(
                    context,
                    context.getString(R.string.empty_bottle),
                    mutableListOf(),
                    false
                )
            )
            addSpritzer(context, "Kisfröccs", "Small spritzer", 1F, 1F)
            addSpritzer(context, "Nagyfröccs", "Big spritzer", 2F, 1F)
            addSpritzer(context, "Hosszúlépés", "Long step", 1F, 2F)
            addSpritzer(context, "Háziúr / Bivalycsók", "", 4F, 1F)
            addSpritzer(context, "Házmester", "", 3F, 2F)
            addSpritzer(context, "Viceházmester", "", 2F, 3F)
            addSpritzer(context, "Sportfröccs", "Sport spritzer", 1F, 4F)
            addSpritzer(context, "Krúdy fröccs", "", 9F, 1F)
            addSpritzer(context, "Avasi fröccs", "", 7F, 3F)
            addSpritzer(context, "Polgármester", "Mayor", 6F, 4F)
            addSpritzer(context, "Maflás", "", 5F, 5F)
            addSpritzer(context, "Alpolgármester", "Deputy mayor", 4F, 6F)
            addSpritzer(context, "Sóherfröccs / Távolugrás", "", 1F, 9F)

            addSpritzer(context, "Kisharapás (Cecéről)", "Small bite", .5F, .5F)
            addSpritzer(context, "Háp-háp (Fehérvárról)", "Quack-quack", 2F, 2F)
            addSpritzer(context, "Előrelépés", "Step forward", 8F, 1F)
            addSpritzer(context, "Puskás fröccs / Magyar-angol", "Puskás spritzer / Hungary-england", 6F, 3F)
            addSpritzer(context, "Góré föccs (pohár bor és egy spriccentésnyi szóda)", "", 1.5F, .5F)
            addSpritzer(context, "Deák föccs (pohár szóda és egy csepp bor)", "", .5F, 1.5F)
            addSpritzer(
                context,
                "Ijesztett / Spricc föccs (sok bor és egy spriccentésnyi szóda)",
                "",
                9.5F,
                .5F
            )

            addSpritzer(context, "Csatos", "", 10F, 5F)
        }

        fun getGlassSizes(): IntArray {
            return recipes
                .map { recipe -> recipe.ingredients.sumOf { (it.amount * 10).roundToInt() } / 10 }
                .distinct()
                .sorted()
                .toIntArray()
        }

        fun getIngredients(): List<Ingredient> {
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

        fun getRecipes(): List<Recipe> {
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

        private fun addSpritzer(context: Context, name: String, englishName: String, wine: Float, soda: Float) {
            recipes.add(
                Recipe(
                    context,
                    name,
                    mutableListOf(
                        Ingredient(
                            context.getString(R.string.ingredient_vine),
                            wine,
                            false
                        ), Ingredient(context.getString(R.string.ingredient_soda), soda, false)
                    ),
                    false
                )
                    .apply { this.englishName = englishName }
            )
        }

        @Deprecated("Rewrite needed")
        private fun save(context: Context) {
            /*
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit().apply {
                putStringSet("ingredients", ingredients.filter { r -> r.isRemovable }.map { r -> r.toString() }.toSet())
                putStringSet("recipes", recipes.filter { r -> r.isRemovable }.map { r -> r.toSavableString() }.toSet())
                apply()
            }
             */
        }
    }
}