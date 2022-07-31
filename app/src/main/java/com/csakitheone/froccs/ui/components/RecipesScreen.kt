package com.csakitheone.froccs.ui.components

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.R
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.data.Recipe

@Preview
@Composable
fun RecipesScreen() {
    var recipes by remember {
        mutableStateOf(Data.getRecipes().filter { it.ingredients.isNotEmpty() }, neverEqualPolicy())
    }

    LazyColumn(modifier = Modifier.padding(8.dp)) {
        item {
            Button(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                onClick = {
                    //TODO
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_barrel), contentDescription = null)
                Text(text = stringResource(id = R.string.cellar))
            }
        }
        items(items = recipes) { recipe ->
            RecipeView(
                recipe = recipe,
                onRefreshRequest = { recipes = Data.getRecipes().filter { it.ingredients.isNotEmpty() } }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeView(recipe: Recipe, onRefreshRequest: () -> Unit) {
    val context = LocalContext.current

    var isMenuVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    if (recipe.isRemovable) isMenuVisible = true
                }
            )
    ) {
        Text(
            modifier = Modifier.padding(2.dp),
            text = recipe.name,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            modifier = Modifier.padding(2.dp),
            text = recipe.getIngredientsString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        DropdownMenu(expanded = isMenuVisible, onDismissRequest = { isMenuVisible = false }) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.remove_ingredient)) },
                onClick = {
                    Data.removeRecipe(context, recipe)
                    isMenuVisible = false
                    onRefreshRequest()
                }
            )
        }
    }
}