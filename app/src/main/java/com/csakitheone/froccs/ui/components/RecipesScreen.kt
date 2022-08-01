package com.csakitheone.froccs.ui.components

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
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
import com.csakitheone.froccs.CellarActivity
import com.csakitheone.froccs.R
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.model.Recipe

@Preview
@Composable
fun RecipesScreen() {
    val context = LocalContext.current

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
                    context.startActivity(Intent(context, CellarActivity::class.java))
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