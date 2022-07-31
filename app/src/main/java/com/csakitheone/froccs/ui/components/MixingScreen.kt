package com.csakitheone.froccs.ui.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.R
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.Ingredient
import com.csakitheone.froccs.data.Recipe
import com.csakitheone.froccs.helper.Helper.Companion.roundToPreference
import kotlin.math.min
import kotlin.math.roundToInt

@Preview
@Composable
fun MixingScreen() {
    val context = LocalContext.current

    var ingredients by remember { mutableStateOf(Data.getIngredients(), neverEqualPolicy()) }
    var recipe: Recipe by remember { mutableStateOf(Data.getRecipes().first()) }
    var amounts by remember { mutableStateOf(mutableMapOf<String, Float>(), neverEqualPolicy()) }

    fun getAmountAsDl(): Float = amounts.values.sum().roundToPreference()

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
                        text = recipe.name,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "${getAmountAsDl()}dl",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                VineBottle(modifier = Modifier.weight(1f).padding(8.dp), fullness = amounts.values.sum())
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { /*TODO share*/ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Divider()
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

@Preview
@Composable
fun VineBottle(modifier: Modifier = Modifier, fullness: Float = 1f) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 48.dp, end = 48.dp, top = 44.dp, bottom = 18.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxWidth()
                    .fillMaxHeight(min(1f, fullness))
            )
            Image(
                painter = painterResource(id = R.drawable.bottle_wine_outline),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    if (isRemovable) isMenuVisible = true
                }
            )
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${amount.roundToPreference()}dl",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Slider(
            value = amount,
            onValueChange = onAmountChange
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

fun findRecipe(context: Context, amounts: MutableMap<String, Float>) : Recipe {
    val ingredients = amounts
        .filter { it.value > 0 }
        .map { Ingredient(it.key, it.value.roundToPreference(), false) }

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