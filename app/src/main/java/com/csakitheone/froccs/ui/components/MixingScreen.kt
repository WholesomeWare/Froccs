package com.csakitheone.froccs.ui.components

import android.content.Context
import android.content.Intent
import android.content.pm.LabeledIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.CellarActivity
import com.csakitheone.froccs.R
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.Temp
import com.csakitheone.froccs.model.Ingredient
import com.csakitheone.froccs.model.Recipe
import com.csakitheone.froccs.helper.Helper.Companion.roundToPreference
import kotlin.math.min

@Preview(showBackground = true)
@Composable
fun MixingScreen() {
    val context = LocalContext.current

    var ingredients by remember { mutableStateOf(Data.getIngredients(), neverEqualPolicy()) }
    var recipe: Recipe? by remember { mutableStateOf(Data.getRecipes().firstOrNull()) }
    var amounts by remember { mutableStateOf(mutableMapOf<String, Float>(), neverEqualPolicy()) }

    fun getAmountAsDl(): Float = amounts.values.sum()

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = recipe?.name ?: "",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "${getAmountAsDl()}dl",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    AnimatedVisibility(
                        visible = recipe?.name == stringResource(id = R.string.no_recipe_found)
                    ) {
                        AddRecipeButton(amounts)
                    }
                    AnimatedVisibility(
                        visible = !recipe?.ingredients.isNullOrEmpty() && recipe?.name != stringResource(id = R.string.no_recipe_found)
                    ) {
                        Button(
                            onClick = {
                                Temp.selectedRecipe = recipe

                                val intent = Intent(Intent.ACTION_SEND)
                                    .putExtra(Intent.EXTRA_TEXT, recipe.toString())
                                    .putExtra(Intent.EXTRA_TITLE, recipe.toString())
                                    .setType("text/plain")
                                val targets = arrayListOf(
                                    LabeledIntent(
                                        Intent(context, CellarActivity::class.java)
                                            .putExtra(Intent.EXTRA_TEXT, recipe.toString())
                                            .putExtra(Intent.EXTRA_SUBJECT, "Add to Cellar")
                                            .setType("text/plain"),
                                        context.packageName,
                                        R.string.add_to_cellar,
                                        R.drawable.ic_barrel
                                    )
                                ).toTypedArray()
                                context.startActivity(
                                    Intent
                                        .createChooser(intent, null)
                                        .putExtra(Intent.EXTRA_INITIAL_INTENTS, targets)
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null
                            )
                        }
                    }
                }
                VineBottle(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    fullness = amounts.values.sum() / 10
                )
            }
            Divider(modifier = Modifier
                .padding(16.dp)
                .alpha(.5f))
        }
        items(items = ingredients) { ingredient ->
            IngredientSlider(
                label = ingredient.name,
                amount = amounts[ingredient.name] ?: 0f,
                onAmountChange = {
                    amounts = amounts.apply { this[ingredient.name] = it }
                    recipe = findRecipe(context, amounts)
                },
                isRemovable = ingredient.isRemovable,
                onRefreshRequest = {
                    ingredients = Data.getIngredients()
                }
            )
        }
        item {
            AddIngredientButton(
                onRefreshRequest = {
                    ingredients = Data.getIngredients()
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IngredientSlider(
    label: String,
    amount: Float = 0f,
    onAmountChange: (Float) -> Unit = {},
    isRemovable: Boolean = true,
    onRefreshRequest: () -> Unit = {}
) {
    val context = LocalContext.current

    var isMenuVisible by remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf(amount / 10) }
    var amountString by remember(amount) {
        mutableStateOf(amount.roundToPreference().toString())
    }

    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    if (isRemovable) isMenuVisible = true
                }
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    modifier = Modifier
                        .clickable { isDialogVisible = true },
                    text = "${amount}dl",
                    color = MaterialTheme.colorScheme.onBackground,
                    textDecoration = TextDecoration.Underline
                )
            }
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    onAmountChange((it * 10).roundToPreference())
                }
            )

            DropdownMenu(expanded = isMenuVisible, onDismissRequest = { isMenuVisible = false }) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.remove_ingredient)) },
                    onClick = {
                        Data.removeIngredient(context, label)
                        isMenuVisible = false
                        onRefreshRequest()
                    }
                )
            }
        }
    }

    if (isDialogVisible) {
        AlertDialog(
            title = { Text(text = label) },
            text = {
                TextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            },
            onDismissRequest = { isDialogVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newAmount = amountString.toFloatOrNull()
                        if (newAmount != null) {
                            sliderValue = newAmount / 10
                            onAmountChange(newAmount)
                            isDialogVisible = false
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.set))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeButton(amounts: MutableMap<String, Float>) {
    val context = LocalContext.current
    val ingredients = amounts
        .filter { it.value >= .2f }
        .map { Ingredient(it.key, it.value, false) }
        .toMutableList()

    var isDialogVisible by remember { mutableStateOf(false) }
    var recipeName by remember { mutableStateOf("") }

    Button(
        onClick = { isDialogVisible = true }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null
        )
        Text(text = stringResource(id = R.string.add_recipe))
    }

    if (isDialogVisible) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.add_recipe)) },
            text = {
                TextField(value = recipeName, onValueChange = { recipeName = it })
            },
            onDismissRequest = { isDialogVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val recipe = Recipe(context, recipeName, ingredients, true)
                        Data.addRecipe(context, recipe)
                        isDialogVisible = false
                        recipeName = ""
                    }
                ) {
                    Text(text = stringResource(id = R.string.add_recipe))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientButton(onRefreshRequest: () -> Unit) {
    val context = LocalContext.current

    var isDialogVisible by remember { mutableStateOf(false) }
    var ingredientName by remember { mutableStateOf("") }

    TextButton(
        onClick = { isDialogVisible = true }
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
        Text(text = stringResource(id = R.string.add_ingredient))
    }

    if (isDialogVisible) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.add_ingredient)) },
            text = {
                TextField(value = ingredientName, onValueChange = { ingredientName = it })
            },
            onDismissRequest = { isDialogVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        Data.addIngredient(context, ingredientName)
                        isDialogVisible = false
                        ingredientName = ""
                        onRefreshRequest()
                    }
                ) {
                    Text(text = stringResource(id = R.string.add_ingredient))
                }
            }
        )
    }
}

fun findRecipe(context: Context, amounts: MutableMap<String, Float>): Recipe {
    val ingredients = amounts
        .filter { it.value >= .2f }
        .map { Ingredient(it.key, it.value, false) }

    if (ingredients.size == 1) {
        return Recipe.Builder(context).setName(ingredients.first().name).build()
    }
    else {
        if (Data.getRecipes().any { r -> r.check(ingredients) }) {
            return Data.getRecipes().first { r -> r.check(ingredients) }
        }
    }

    val shareable = Recipe.Builder(context).setName(context.getString(R.string.no_recipe_found))
    ingredients.map { r -> shareable.addIngredient(r) }
    return shareable.build()
}