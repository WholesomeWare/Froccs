package com.csakitheone.froccs

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.children
import androidx.preference.PreferenceManager
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.Ingredient
import com.csakitheone.froccs.data.Recipe
import com.csakitheone.froccs.databinding.ActivityMainBinding
import com.csakitheone.froccs.helper.Workshop
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    lateinit var prefs: SharedPreferences
    var currentIngredients = mutableListOf<Ingredient>()
    var restartNeeded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        binding.mainToolbar.setOnMenuItemClickListener {
            if (it.title == "Beállítások") {
                startActivity(Intent(this, SettingsActivity::class.java))
                restartNeeded = true
                return@setOnMenuItemClickListener true
            }
            false
        }

        binding.mainNav.setOnItemSelectedListener {
            binding.mainLayoutDrink.visibility = View.GONE
            binding.mainLayoutRecipes.visibility = View.GONE
            binding.mainLayoutExtras.visibility = View.GONE
            when (it.itemId) {
                R.id.menuMainNavDrink -> binding.mainLayoutDrink.visibility = View.VISIBLE
                R.id.menuMainNavRecipes -> binding.mainLayoutRecipes.visibility = View.VISIBLE
                R.id.menuMainNavExtras -> binding.mainLayoutExtras.visibility = View.VISIBLE
                else -> return@setOnItemSelectedListener false
            }
            true
        }
        binding.mainNav.selectedItemId = savedInstanceState?.getInt("navSelectedItemId") ?: R.id.menuMainNavDrink

        Data.loadUserData(this)
        loadAds()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("navSelectedItemId", binding.mainNav.selectedItemId)
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
        binding.mainBanner.loadAd(AdRequest.Builder().build())
    }

    fun loadIngredients() {
        currentIngredients = Data.getIngredients().toMutableList()

        binding.mainGroupIngredients.removeAllViews()
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
            binding.mainGroupIngredients.addView(v)
        }

        binding.mainLayoutDrinkIngredients.removeAllViews()
        currentIngredients.map {
            val v = it.createView(this) { s: String, i: Float ->
                refreshMaximums()
                findRecipe()
            }
            binding.mainLayoutDrinkIngredients.addView(v)
        }
        refreshMaximums()
        findRecipe()
    }

    fun refreshMaximums() {
        binding.mainProgress.progress = currentIngredients.sumBy { r -> (r.amount * Ingredient.AMOUNT_PRECISION).toInt() }

        if (prefs.getBoolean("pref_no_limit", false)) return

        for (v in binding.mainLayoutDrinkIngredients.children) {
            val x = currentIngredients.filter { r -> r.name != v.findViewById<TextView>(R.id.ingredientText).text.split(':')[0] }.sumBy { r -> (r.amount * Ingredient.AMOUNT_PRECISION).toInt() }
            v.findViewById<SeekBar>(R.id.ingredientSeek).max = 100 - x
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
        binding.mainTextRecipeName.text = recipe.name + "\n\n" + currentIngredients.sumByDouble { r -> r.amount.toDouble() }.toFloat() + (if (prefs.getBoolean("pref_show_dl", true)) "dl" else "")
        binding.mainBtnNewRecipe.isEnabled = recipe.name == "Nincs ilyen recept"

        val shareable = Recipe.Builder(this).setName(recipe.name)
        currentIngredients.filter { r -> r.amount > 0 }.map { r -> shareable.addIngredient(r) }
        return shareable.build()
    }

    fun loadRecipes() {
        binding.mainTextRecipesTitle.text = "Receptek (${Data.getRecipes().size})"
        binding.mainComposeRecipes.setContent {
            Column(Modifier.padding(8.dp)) {
                Data.getRecipes().map {
                    RecipeItem(recipe = it)
                }
            }
        }
        findRecipe()
    }

    @Composable
    fun RecipeItem(modifier: Modifier = Modifier, recipe: Recipe) {
        Row(
            modifier
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f).padding(8.dp)) {
                Text(text = recipe.name, fontWeight = FontWeight.Medium)
                Text(text = recipe.ingredients.joinToString(), fontSize = 12.sp)
            }
            if (recipe.isRemovable) {
                IconButton(onClick = {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle(recipe.name)
                        .setMessage("Biztos törlöd ezt a receptet?")
                        .setPositiveButton("Igen") { _: DialogInterface, _: Int ->
                            Data.removeRecipe(this@MainActivity, recipe)
                            loadRecipes()
                        }
                        .setNegativeButton("Nem") { _: DialogInterface, _: Int -> }
                        .create().show()
                }) {
                    Image(painter = painterResource(R.drawable.ic_close), contentDescription = "Close button")
                }
            }
        }
    }

    @Preview
    @Composable
    fun RecipeItemPreview() {
        RecipeItem(recipe = Recipe(this, "Kólavíz", mutableListOf(Ingredient("kóla"), Ingredient("víz")), true))
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

    fun btnGameClick(view: View) {
        startActivity(Intent(this, GameActivity::class.java))
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

    fun btnSupportVideoClick(view: View) {
        startActivity(Intent(this, RewardedAdActivity::class.java))
    }
}