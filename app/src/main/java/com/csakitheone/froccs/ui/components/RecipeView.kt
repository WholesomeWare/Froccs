package com.csakitheone.froccs.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun RecipeView(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                text = recipe.name,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = recipe.getIngredientsString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5f),
                )
                LinearProgressIndicator(
                    modifier = Modifier.width(120.dp),
                    progress = { recipe.getRatio() },
                )
            }
        }
    }
}