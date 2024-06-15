package com.csakitheone.froccs.ui.tabs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.csakitheone.froccs.R
import com.csakitheone.froccs.data.Data
import com.csakitheone.froccs.model.Recipe
import com.csakitheone.froccs.ui.components.VineBottle
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
@Preview
fun TabFroccs() {
    val context = LocalContext.current
    var selectedGlassSize by remember { mutableIntStateOf(2) }
    var ingredientsRatio by remember(selectedGlassSize) { mutableFloatStateOf(0f) }
    val amountSoda by remember(selectedGlassSize, ingredientsRatio) {
        mutableFloatStateOf(
            selectedGlassSize - ((ingredientsRatio * 2).roundToInt() / 2f)
        )
    }
    val amountVine by remember(selectedGlassSize, ingredientsRatio) {
        mutableFloatStateOf(
            (ingredientsRatio * 2).roundToInt() / 2f
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
                .forEach { glassSize ->
                    FilterChip(
                        modifier = Modifier
                            .padding(16.dp)
                            .graphicsLayer {
                                scaleX = if (glassSize == selectedGlassSize) 1.2f else 1f
                                scaleY = if (glassSize == selectedGlassSize) 1.2f else 1f
                            },
                        label = { Text(text = "${glassSize}dl") },
                        selected = glassSize == selectedGlassSize,
                        onClick = {
                            selectedGlassSize = glassSize
                        },
                    )
                }
        }

        VineBottle(
            modifier = Modifier.size(250.dp),
            fullness = selectedGlassSize / 10f,
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

        Box(
            modifier = Modifier.padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Slider(
                value = ingredientsRatio,
                onValueChange = { ingredientsRatio = it },
                valueRange = 0f..selectedGlassSize.toFloat(),
            )
            Data.getRecipes()
                .filter { recipe ->
                    recipe.getSize() == selectedGlassSize.toFloat()
                }
                .forEach { recipe ->
                    val ratio =
                        (recipe.ingredients.firstOrNull { it.name == context.getString(R.string.ingredient_vine) }?.amount
                            ?: 0f) / recipe.getSize()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Spacer(
                            modifier = Modifier.fillMaxWidth(ratio)
                        )
                        Surface(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape),
                            color = if (ingredientsRatio > ratio * recipe.getSize()) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.primary,
                        ) {}
                    }
                }
        }
    }
}