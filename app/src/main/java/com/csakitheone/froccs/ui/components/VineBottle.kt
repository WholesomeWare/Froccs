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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.csakitheone.froccs.R
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.min
import kotlin.math.roundToInt

@Preview
@Composable
fun VineBottle(
    modifier: Modifier = Modifier,
    fullness: Float = 1f,
) {
    val smoothFullness by animateFloatAsState(targetValue = fullness)
    var waveInteracted by remember { mutableStateOf(false) }
    LaunchedEffect(fullness) { waveInteracted = true }
    LaunchedEffect(waveInteracted) { waveInteracted = false }
    val waveIntensity by animateFloatAsState(
        targetValue = if (waveInteracted) 5f else 0f,
        animationSpec = tween(if (waveInteracted) 0 else 1200)
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = { waveInteracted = true })
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min),
            contentAlignment = Alignment.Center
        ) {
            WaveAnimation(
                modifier = Modifier
                    .width(50.dp)
                    .height(130.dp)
                    .offset(y = 10.dp),
                color = MaterialTheme.colorScheme.secondary,
                progress = smoothFullness,
                amplitudeMultiplier = waveIntensity
            )
            Image(
                modifier = Modifier.aspectRatio(1 / 1.6f),
                painter = painterResource(id = R.drawable.bottle_wine_outline),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
                contentScale = ContentScale.FillHeight
            )
        }
    }
}
