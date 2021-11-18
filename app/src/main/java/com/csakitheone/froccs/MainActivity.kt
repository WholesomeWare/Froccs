package com.csakitheone.froccs

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.preference.PreferenceManager
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.Ingredient
import com.csakitheone.froccs.data.Recipe
import com.csakitheone.froccs.helper.Workshop
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_ingredient.view.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    var currentIngredients: MutableList<Ingredient> = mutableListOf()
    var restartNeeded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        mainToolbar.setOnMenuItemClickListener {
            if (it.title == "Beállítások") {
                startActivity(Intent(this, SettingsActivity::class.java))
                restartNeeded = true
                return@setOnMenuItemClickListener true
            }
            false
        }

        Data.loadUserData(this)
        loadAds()
    }

    override fun onResume() {
        super.onResume()
        loadIngredients()
        loadRecipes()
        if(restartNeeded) {
            restartNeeded = false
            recreate()
        }
    }

    fun loadAds() {
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("24E9E518AB9DBE2924B9B93F22361702"))
                .build()
        )
        MobileAds.initialize(this)
        mainBanner.loadAd(AdRequest.Builder().build())
    }

    fun loadIngredients() {
        mainLayoutIngredients.removeAllViews()
        currentIngredients = Data.getIngredients().toMutableList()
        currentIngredients.map {
            val v = it.createView(this) { s: String, i: Float ->
                refreshMaximums()
                findRecipe()
            }
            v.ingredientBtnRemove.setOnClickListener { view ->
                AlertDialog.Builder(this)
                        .setTitle(it.name)
                        .setMessage("Biztos törlöd ezt az alapanyagot?")
                        .setPositiveButton("Igen") { _: DialogInterface, _: Int ->
                            Data.removeIngredient(this, it)
                            loadIngredients()
                        }
                        .setNegativeButton("Nem") { _: DialogInterface, _: Int -> }
                        .create().show()
            }
            mainLayoutIngredients.addView(v)
        }
        refreshMaximums()
        findRecipe()
    }

    fun refreshMaximums() {
        mainProgress.progress = currentIngredients.sumBy { r -> (r.amount * Ingredient.AMOUNT_PRECISION).toInt() }

        if (prefs.getBoolean("pref_no_limit", false)) return

        for (v in mainLayoutIngredients.children) {
            val x = currentIngredients.filter { r -> r.name != v.ingredientText.text.split(':')[0] }.sumBy { r -> (r.amount * Ingredient.AMOUNT_PRECISION).toInt() }
            v.ingredientSeek.max = 100 - x
        }
    }

    fun findRecipe() : Recipe {
        var recipe = Recipe(this, "Nincs ilyen recept", mutableListOf())

        if (currentIngredients.filter { r -> r.amount > 0 }.size == 1) {
            recipe = Recipe.Builder(this)
                .setName(currentIngredients.filter { r -> r.amount > 0 }[0].name)
                .build()
        }
        else {
            if (Data.getRecipes().any { r -> r.check(currentIngredients) }) {
                recipe = Data.getRecipes().first { r -> r.check(currentIngredients) }
            }
        }
        mainTextRecipeName.text = recipe.name + "\n\n" + currentIngredients.sumByDouble { r -> r.amount.toDouble() }.toFloat() + (if (prefs.getBoolean("pref_show_dl", true)) "dl" else "")
        mainBtnNewRecipe.isEnabled = recipe.name == "Nincs ilyen recept"

        val shareable = Recipe.Builder(this).setName(recipe.name)
        currentIngredients.filter { r -> r.amount > 0 }.map { r -> shareable.addIngredient(r) }
        return shareable.build()
    }

    fun loadRecipes() {
        mainLayoutRecipes.removeAllViews()
        Data.getRecipes().filter { r -> r.isRemovable }.map {
            val v = TextView(this)
            v.text = it.name
            v.setPadding(20)
            val backgroundId = TypedValue()
            theme.resolveAttribute(R.attr.selectableItemBackground, backgroundId, true)
            v.background = ContextCompat.getDrawable(this, backgroundId.resourceId)
            v.setOnClickListener { view ->
                AlertDialog.Builder(this)
                    .setTitle(it.name)
                    .setMessage(it.toString().split('\n')[1])
                    .setNegativeButton("Törlés") { _: DialogInterface, _: Int ->
                        Data.removeRecipe(this, it)
                        loadRecipes()
                    }
                    .create().show()
            }
            mainLayoutRecipes.addView(v)
        }
        findRecipe()
    }

    fun btnShareClick(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Hol szeretnéd megosztani a receptet?")
            .setPositiveButton("Pince") { _: DialogInterface, _: Int ->
                Workshop.addString(Workshop.WORKSHOP_CATEGORY_RECIPE, findRecipe().toString()) {
                    startActivity(Intent(this, WorkshopActivity::class.java))
                }
            }
            .setNegativeButton("Más alkalmazás") { _: DialogInterface, _: Int ->
                val intent= Intent()
                intent.action=Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, findRecipe().toString())
                intent.type="text/plain"
                startActivity(Intent.createChooser(intent, "Ital megosztása"))
            }
            .setNeutralButton("Mégsem") { _: DialogInterface, _: Int ->}
            .create().show()
    }

    fun btnNewRecipeClick(view: View) {
        val editRecipeName = EditText(this)
        editRecipeName.hint = "Pl: Háp-háp"
        AlertDialog.Builder(this)
            .setTitle("Új recept")
            .setView(editRecipeName)
            .setPositiveButton("Hozzáadás") { _: DialogInterface, _: Int ->
                if (editRecipeName.text.isEmpty()) {
                    Toast.makeText(this, "Nem lehet üres a recept neve!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                Data.addRecipe(this,
                    Recipe.Builder(this)
                        .setName(editRecipeName.text.toString())
                        .addAllIngredients(currentIngredients)
                        .build()
                )
                loadRecipes()
            }
            .create().show()
    }

    fun btnWorkshopClick(view: View) {
        startActivity(Intent(this, WorkshopActivity::class.java))
    }

    fun btnGlassholderClick(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Pohártartó")
            .setMessage("Ezt a funkciót csak kisebb poharakkal érdemes használni és légy nagyon óvatos, hogy a telefon sértetlen maradjon! Csak akkor használd a pohártartót, ha vállalod a következményeket!")
            .setPositiveButton("Értem és vállalom") { _: DialogInterface, _: Int ->
                startActivity(Intent(this, GlassholderActivity::class.java))
            }
            .setNegativeButton("Vissza") { _: DialogInterface, _: Int -> }
            .create().show()
    }

    fun btnNewIngredientClick(view: View) {
        val editIngredientName = EditText(this)
        editIngredientName.hint = "Pl: málna szörp"
        AlertDialog.Builder(this)
            .setTitle("Új alapanyag")
            .setView(editIngredientName)
            .setPositiveButton("Hozzáadás") { _: DialogInterface, _: Int ->
                if (Data.getIngredients().any { r -> r.name == editIngredientName.text.toString() }) {
                    Toast.makeText(this, "Már van ilyen alapanyag!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                else if (editIngredientName.text.isEmpty()) {
                    Toast.makeText(this, "Nem lehet üres az alapanyag neve!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                Data.addIngredient(this, Ingredient(editIngredientName.text.toString(), 0F))
                loadIngredients()
            }
            .create().show()
    }

    fun textRecipesUnder4Click(view: View) {
        val recipes = Data.getRecipes()
            .filter { r -> !r.isRemovable && r.name != "Üres pohár" && !r.name.contains("(") && r.getSize() < 4 }
            .joinToString("\n\n") { r -> r.toString() }
        AlertDialog.Builder(this)
            .setTitle("Arányok 4 deci alatt")
            .setMessage(recipes)
            .create().show()
    }

    fun textRecipes5Click(view: View) {
        val recipes = Data.getRecipes()
            .filter { r -> !r.isRemovable && r.name != "Üres pohár" && !r.name.contains("(") && r.getSize() == 5F }
            .joinToString("\n\n") { r -> r.toString() }
        AlertDialog.Builder(this)
            .setTitle("Fél literes arányok")
            .setMessage(recipes)
            .create().show()
    }

    fun textRecipes1Click(view: View) {
        val recipes = Data.getRecipes()
            .filter { r -> !r.isRemovable && r.name != "Üres pohár" && !r.name.contains("(") && r.getSize() == 10F }
            .joinToString("\n\n") { r -> r.toString() }
        AlertDialog.Builder(this)
            .setTitle("Literes arányok")
            .setMessage(recipes)
            .create().show()
    }

    fun textRecipesExtensionClick(view: View) {
        val recipes = Data.getRecipes()
            .filter { r -> !r.isRemovable && r.name.contains("(") }
            .joinToString("\n\n") { r -> r.toString() }
        AlertDialog.Builder(this)
            .setTitle("Kevésbé ismert fröccsök")
            .setMessage(recipes)
            .create().show()
    }
}