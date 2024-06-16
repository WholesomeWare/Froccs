package com.csakitheone.froccs.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.R
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.model.Recipe

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeView(recipe: Recipe, onRefreshRequest: () -> Unit = {}) {
    val context = LocalContext.current

    var isInfoDialogVisible by remember { mutableStateOf(false) }
    var isMenuVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    if (!recipe.isRemovable) isInfoDialogVisible = true
                },
                onLongClick = {
                    if (recipe.isRemovable) isMenuVisible = true
                }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(8.dp).weight(1f),
                text = recipe.name,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = recipe.getIngredientsString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            DropdownMenu(expanded = isMenuVisible, onDismissRequest = { isMenuVisible = false }) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.remove_recipe)) },
                    onClick = {
                        Data.removeRecipe(context, recipe)
                        isMenuVisible = false
                        onRefreshRequest()
                    }
                )
            }
        }
    }

    if (isInfoDialogVisible) {
        AlertDialog(
            title = { Text(text = recipe.name) },
            text = {
                Column {
                    if (recipe.englishName.isNotEmpty()) {
                        Text(modifier = Modifier.padding(8.dp), text = recipe.englishName)
                    }
                    Text(modifier = Modifier.padding(8.dp), text = recipe.getIngredientsString())
                }
            },
            onDismissRequest = { isInfoDialogVisible = false },
            confirmButton = {
                TextButton(onClick = { isInfoDialogVisible = false }) {
                    Text(text = "Ok")
                }
            }
        )
    }
}