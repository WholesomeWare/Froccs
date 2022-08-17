package com.csakitheone.froccs.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.csakitheone.froccs.R
import kotlin.math.min
import kotlin.math.roundToInt

@Preview
@Composable
fun VineBottle(modifier: Modifier = Modifier, fullness: Float = 1f) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min),
            contentAlignment = Alignment.Center
        ) {
            /*
            Box(
                modifier = Modifier
                    .padding(start = 64.dp, end = 64.dp, top = 60.dp, bottom = 18.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .fillMaxWidth()
                    .fillMaxHeight(min(1f, fullness))
            )
             */
            Wave(
                modifier = Modifier
                    .width(36.dp)
                    .height(100.dp)
                    .offset(y = 10.dp),
                widthValue = 36.dp.value,
                height = fullness,
                waveSize = 15,
                waveIntensity = .3f
            )
            Image(
                painter = painterResource(id = R.drawable.bottle_wine_outline),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

@Preview
@Composable
fun Wave(
    modifier: Modifier = Modifier.size(80.dp),
    widthValue: Float = 80.dp.value,
    height: Float = .8f,
    waveSize: Int = 32,
    waveIntensity: Float = .5f
) {
    var waveActivated by remember { mutableStateOf(false) }
    LaunchedEffect(waveActivated) { waveActivated = false }
    val waveTime by animateFloatAsState(
        targetValue = if (waveActivated) 1f else 0f,
        animationSpec = tween(if (waveActivated) 0 else 3000, easing = LinearEasing)
    )
    val wavesOffsetTransition = rememberInfiniteTransition()
    val wavesOffset by wavesOffsetTransition.animateFloat(
        initialValue = 0f,
        targetValue = waveSize * 5f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 800, easing = LinearEasing)
        )
    )
    val wavesScrollState = rememberScrollState()
    LaunchedEffect(wavesOffset) {
        wavesScrollState.scrollTo(wavesOffset.toInt())
    }

    // Container
    Column(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        waveActivated = true
                    }
                )
            },
        verticalArrangement = Arrangement.Bottom
    ) {
        // Waves
        Row(
            modifier = Modifier
                .horizontalScroll(wavesScrollState)
                .offset(y = (waveSize * waveTime / 2).dp)
                .zIndex(1f)
        ) {
            @Composable
            fun WavePart(parity: Int) {
                Box(
                    modifier = Modifier
                        .scale(1f, waveIntensity * waveTime)
                        .clip(CircleShape)
                        .background(
                            if (parity % 2 == 0)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.background
                        )
                        .size((waveSize * waveTime).dp)
                )
            }
            for (i in 0 until 6) {
                WavePart(parity = i)
            }
        }
        // Static liqid
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .fillMaxWidth()
                .fillMaxHeight(min(1f, height))
                .zIndex(0f)
        )
    }
}
