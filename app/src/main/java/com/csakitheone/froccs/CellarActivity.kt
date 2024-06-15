package com.csakitheone.froccs

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.data.FSDB
import com.csakitheone.froccs.data.Temp
import com.csakitheone.froccs.model.Recipe
import com.csakitheone.froccs.ui.components.RecipeView
import com.csakitheone.froccs.ui.theme.FröccsTheme

class CellarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CellarScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun CellarScreen() {
        var recipes by remember { mutableStateOf(listOf<Recipe>(), neverEqualPolicy()) }
        var isShareDialogVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            FSDB.getRecipes(this@CellarActivity) {
                recipes = it
            }
            if (!intent.getStringExtra(Intent.EXTRA_TEXT).isNullOrEmpty()) {
                isShareDialogVisible = true
            }
        }

        FröccsTheme {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    CenterAlignedTopAppBar(
                        title = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = stringResource(R.string.cellar))
                                Text(
                                    text = stringResource(R.string.user_created_drinks),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                if (recipes.isEmpty()) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
                items(items = recipes) { recipe ->
                    RecipeView(recipe = recipe)
                }
            }

            if (isShareDialogVisible) {
                AlertDialog(
                    title = { Text(text = stringResource(id = R.string.add_to_cellar)) },
                    text = { Text(text = intent.getStringExtra(Intent.EXTRA_TEXT).toString()) },
                    onDismissRequest = {
                        isShareDialogVisible = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                recipes = listOf()
                                FSDB.addRecipe(Temp.selectedRecipe) {
                                    FSDB.getRecipes(this) {
                                        recipes = it
                                    }
                                }
                                isShareDialogVisible = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.add_to_cellar))
                        }
                    }
                )
            }
        }
    }
}