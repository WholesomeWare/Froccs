package com.csakitheone.froccs

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.preference.PreferenceManager
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.Ingredient
import com.csakitheone.froccs.data.Recipe
import com.csakitheone.froccs.helper.Workshop
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_ingredient.view.*

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

        mainNav.setOnItemSelectedListener {
            mainLayoutDrink.visibility = View.GONE
            mainLayoutRecipes.visibility = View.GONE
            mainLayoutExtras.visibility = View.GONE
            when (it.itemId) {
                R.id.menuMainNavDrink -> mainLayoutDrink.visibility = View.VISIBLE
                R.id.menuMainNavRecipes -> mainLayoutRecipes.visibility = View.VISIBLE
                R.id.menuMainNavExtras -> mainLayoutExtras.visibility = View.VISIBLE
                else -> return@setOnItemSelectedListener false
            }
            true
        }
        mainNav.selectedItemId = savedInstanceState?.getInt("navSelectedItemId") ?: R.id.menuMainNavDrink

        Data.loadUserData(this)
        loadAds()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("navSelectedItemId", mainNav.selectedItemId)
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
                .setTestDeviceIds(listOf("A95A3A512D1FE5693AE2EF06BAFC5E42"))
                .build()
        )
        MobileAds.initialize(this)
        mainBanner.loadAd(AdRequest.Builder().build())
    }

    fun loadIngredients() {
        currentIngredients = Data.getIngredients().toMutableList()

        mainGroupIngredients.removeAllViews()
        currentIngredients.map {
            val v = Chip(this).apply {
                text = it.name
                isCheckable = false
                isCloseIconVisible = it.name != "bor" && it.name != "szóda"
                setOnCloseIconClickListener { _ ->
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle(it.name)
                        .setMessage("Biztos törlöd ezt az alapanyagot?")
                        .setPositiveButton("Igen") { _: DialogInterface, _: Int ->
                            Data.removeIngredient(this@MainActivity, it)
                            loadIngredients()
                        }
                        .setNegativeButton("Nem") { _: DialogInterface, _: Int -> }
                        .create().show()
                }
            }
            mainGroupIngredients.addView(v)
        }

        mainLayoutDrinkIngredients.removeAllViews()
        currentIngredients.map {
            val v = it.createView(this) { s: String, i: Float ->
                refreshMaximums()
                findRecipe()
            }
            mainLayoutDrinkIngredients.addView(v)
        }
        refreshMaximums()
        findRecipe()
    }

    fun refreshMaximums() {
        mainProgress.progress = currentIngredients.sumBy { r -> (r.amount * Ingredient.AMOUNT_PRECISION).toInt() }

        if (prefs.getBoolean("pref_no_limit", false)) return

        for (v in mainLayoutDrinkIngredients.children) {
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
        mainTextRecipesTitle.text = "Receptek (${Data.getRecipes().size})"
        mainGroupRecipes.removeAllViews()
        Data.getRecipes().map {
            val v = Chip(this).apply {
                text = it.name.split("(")[0]
                isCheckable = false
                isCloseIconVisible = it.isRemovable
                setOnClickListener { _ ->
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle(it.name)
                        .setMessage(it.toString().split('\n')[1])
                        .create().show()
                }
                setOnCloseIconClickListener { _ ->
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle(it.name)
                        .setMessage("Biztos törlöd ezt a receptet?")
                        .setPositiveButton("Igen") { _: DialogInterface, _: Int ->
                            Data.removeRecipe(this@MainActivity, it)
                            loadRecipes()
                        }
                        .setNegativeButton("Nem") { _: DialogInterface, _: Int -> }
                        .create().show()
                }
            }
            mainGroupRecipes.addView(v)
        }
        findRecipe()
    }

    fun btnShareClick(view: View) {
        MaterialAlertDialogBuilder(this)
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
        MaterialAlertDialogBuilder(this)
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
        MaterialAlertDialogBuilder(this)
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
        MaterialAlertDialogBuilder(this)
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
        MaterialAlertDialogBuilder(this)
            .setTitle("Arányok 4 deci alatt")
            .setMessage(recipes)
            .create().show()
    }

    fun textRecipes5Click(view: View) {
        val recipes = Data.getRecipes()
            .filter { r -> !r.isRemovable && r.name != "Üres pohár" && !r.name.contains("(") && r.getSize() == 5F }
            .joinToString("\n\n") { r -> r.toString() }
        MaterialAlertDialogBuilder(this)
            .setTitle("Fél literes arányok")
            .setMessage(recipes)
            .create().show()
    }

    fun textRecipes1Click(view: View) {
        val recipes = Data.getRecipes()
            .filter { r -> !r.isRemovable && r.name != "Üres pohár" && !r.name.contains("(") && r.getSize() == 10F }
            .joinToString("\n\n") { r -> r.toString() }
        MaterialAlertDialogBuilder(this)
            .setTitle("Literes arányok")
            .setMessage(recipes)
            .create().show()
    }

    fun textRecipesExtensionClick(view: View) {
        val recipes = Data.getRecipes()
            .filter { r -> !r.isRemovable && r.name.contains("(") }
            .joinToString("\n\n") { r -> r.toString() }
        MaterialAlertDialogBuilder(this)
            .setTitle("Kevésbé ismert fröccsök")
            .setMessage(recipes)
            .create().show()
    }
}