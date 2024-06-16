package com.csakitheone.froccs.ui.tabs

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.R
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.ui.components.VineBottle
import kotlin.math.roundToInt

@Composable
@Preview
fun TabFroccs() {
    val context = LocalContext.current
    var glassSize by remember { mutableIntStateOf(2) }
    var ingredientsRatio by remember(glassSize) { mutableFloatStateOf(0f) }
    val amountVine by remember(glassSize, ingredientsRatio) {
        mutableFloatStateOf(
            (ingredientsRatio * glassSize * 2).roundToInt() / 2f
        )
    }
    val amountSoda by remember(glassSize, ingredientsRatio) {
        mutableFloatStateOf(
            glassSize - ((ingredientsRatio * glassSize * 2).roundToInt() / 2f)
        )
    }
    val recipe by remember(amountSoda, amountVine) {
        mutableStateOf(
            Data.getRecipes().firstOrNull { recipe ->
                recipe.ingredients.firstOrNull { it.name == context.getString(R.string.ingredient_soda) }?.amount == amountSoda &&
                        recipe.ingredients.firstOrNull { it.name == context.getString(R.string.ingredient_vine) }?.amount == amountVine
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
        ) {
            Data.getGlassSizes()
                .filter { it != 0 }
                .forEach { size ->
                    FilterChip(
                        modifier = Modifier
                            .padding(16.dp)
                            .graphicsLayer {
                                scaleX = if (glassSize == size) 1.2f else 1f
                                scaleY = if (glassSize == size) 1.2f else 1f
                            },
                        label = { Text(text = "${size}dl") },
                        selected = glassSize == size,
                        onClick = {
                            glassSize = size
                        },
                    )
                }
        }

        VineBottle(
            modifier = Modifier.size(250.dp),
            fullness = glassSize / 10f,
        )

        Text(
            modifier = Modifier.padding(16.dp),
            text = "${amountVine}dl ${stringResource(id = R.string.ingredient_vine)}, ${amountSoda}dl ${
                stringResource(
                    id = R.string.ingredient_soda
                )
            }".replace(".0", "")
        )

        AnimatedContent(targetState = recipe) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = it?.name ?: "",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier.padding(32.dp),
        ) {
            Box(
                modifier = Modifier.offset(y = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Data.getRecipes()
                    .filter { it.getSize() == glassSize.toFloat() }
                    .forEach { recipe ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .fillMaxWidth(),
                        ) {
                            Spacer(
                                modifier = Modifier.fillMaxWidth(recipe.getRatio())
                            )
                            Surface(
                                modifier = Modifier
                                    .size(8.dp)
                                    .offset(x = (-4).dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary
                            ) {}
                        }
                    }
            }

            Slider(
                value = ingredientsRatio,
                onValueChange = { ingredientsRatio = it },
                steps = glassSize * 2 - 1,
            )
        }
    }
}